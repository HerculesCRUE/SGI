import { Component, OnInit, ViewChild } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { AbstractTablePaginationComponent } from '@core/component/abstract-table-pagination.component';
import { IComite } from '@core/models/eti/comite';
import { IEvaluacion } from '@core/models/eti/evaluacion';
import { TipoConvocatoriaReunion } from '@core/models/eti/tipo-convocatoria-reunion';
import { TipoEvaluacion } from '@core/models/eti/tipo-evaluacion';
import { FxFlexProperties } from '@core/models/shared/flexLayout/fx-flex-properties';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { ComiteService } from '@core/services/eti/comite.service';
import { EvaluacionService } from '@core/services/eti/evaluacion.service';
import { TipoConvocatoriaReunionService } from '@core/services/eti/tipo-convocatoria-reunion.service';
import { TipoEvaluacionService } from '@core/services/eti/tipo-evaluacion.service';
import { PersonaService } from '@core/services/sgp/persona.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { RSQLSgiRestFilter, SgiRestFilter, SgiRestFilterOperator, SgiRestListResult } from '@sgi/framework/http';
import { TipoColectivo } from 'src/app/esb/sgp/shared/select-persona/select-persona.component';
import { NGXLogger } from 'ngx-logger';
import { Observable, of, zip } from 'rxjs';
import { catchError, map, startWith, switchMap } from 'rxjs/operators';

const MSG_ERROR = marker('error.load');

@Component({
  selector: 'sgi-gestion-seguimiento-listado',
  templateUrl: './gestion-seguimiento-listado.component.html',
  styleUrls: ['./gestion-seguimiento-listado.component.scss']
})
export class GestionSeguimientoListadoComponent extends AbstractTablePaginationComponent<IEvaluacion> implements OnInit {

  fxFlexProperties: FxFlexProperties;
  fxLayoutProperties: FxLayoutProperties;

  displayedColumns: string[];

  @ViewChild(MatSort, { static: true }) sort: MatSort;
  @ViewChild(MatPaginator, { static: false }) paginator: MatPaginator;

  evaluaciones$: Observable<IEvaluacion[]> = of();

  private comiteListado: IComite[];
  private tipoEvaluacionListado: TipoEvaluacion[];
  private tipoConvocatoriaReunionListado: TipoConvocatoriaReunion[];


  filteredComites: Observable<IComite[]>;
  filteredTipoEvaluacion: Observable<TipoEvaluacion[]>;
  filteredTipoConvocatoriaReunion: Observable<TipoConvocatoriaReunion[]>;

  get tipoColectivoSolicitante() {
    return TipoColectivo.SOLICITANTE_ETICA;
  }

  constructor(
    private readonly logger: NGXLogger,
    private readonly evaluacionesService: EvaluacionService,
    protected readonly snackBarService: SnackBarService,
    private readonly comiteService: ComiteService,
    private readonly tipoEvaluacionService: TipoEvaluacionService,
    private readonly tipoConvocatoriaReunionService: TipoConvocatoriaReunionService,
    protected readonly personaService: PersonaService
  ) {

    super(snackBarService, MSG_ERROR);

    this.suscripciones = [];

    this.totalElementos = 0;

    this.fxFlexProperties = new FxFlexProperties();
    this.fxFlexProperties.sm = '0 1 calc(50%-10px)';
    this.fxFlexProperties.md = '0 1 calc(33%-10px)';
    this.fxFlexProperties.gtMd = '0 1 calc(13%-10px)';
    this.fxFlexProperties.order = '2';

    this.fxLayoutProperties = new FxLayoutProperties();
    this.fxLayoutProperties.gap = '20px';
    this.fxLayoutProperties.layout = 'row wrap';
    this.fxLayoutProperties.xs = 'column';
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.formGroup = new FormGroup({
      comite: new FormControl('', []),
      fechaEvaluacionInicio: new FormControl(null, []),
      fechaEvaluacionFin: new FormControl(null, []),
      referenciaMemoria: new FormControl('', []),
      tipoConvocatoriaReunion: new FormControl('', []),
      solicitante: new FormControl('', []),
      tipoEvaluacion: new FormControl('', [])
    });

    this.loadComites();
    this.loadTipoEvaluacion();
    this.loadConvocatoriasReunion();
  }

  protected createObservable(reset?: boolean): Observable<SgiRestListResult<IEvaluacion>> {
    const observable$ = this.evaluacionesService.findSeguimientoMemoria(this.getFindOptions(reset));
    return observable$;
  }

  protected initColumns(): void {
    this.displayedColumns = ['memoria.comite.comite', 'tipoEvaluacion', 'fechaDictamen', 'memoria.numReferencia', 'solicitante',
      'dictamen.nombre', 'version', 'acciones'];
  }

  protected createFilter(): SgiRestFilter {
    const controls = this.formGroup.controls;
    const filter = new RSQLSgiRestFilter('memoria.comite.id', SgiRestFilterOperator.EQUALS, controls.comite.value?.id?.toString())
      .and('tipoEvaluacion.id', SgiRestFilterOperator.EQUALS, controls.tipoEvaluacion.value?.id?.toString())
      .and('fechaDictamen', SgiRestFilterOperator.GREATHER_OR_EQUAL, LuxonUtils.toBackend(controls.fechaEvaluacionInicio.value))
      .and('fechaDictamen', SgiRestFilterOperator.LOWER_OR_EQUAL, LuxonUtils.toBackend(controls.fechaEvaluacionFin.value))
      .and('memoria.numReferencia', SgiRestFilterOperator.LIKE_ICASE, controls.referenciaMemoria.value)
      .and(
        'convocatoriaReunion.tipoConvocatoriaReunion.id',
        SgiRestFilterOperator.EQUALS,
        controls.tipoConvocatoriaReunion.value?.id?.toString()
      ).and('memoria.peticionEvaluacion.personaRef', SgiRestFilterOperator.EQUALS, controls.solicitante.value.id);

    return filter;
  }

  protected loadTable(reset?: boolean) {
    // Do the request with paginator/sort/filter values
    this.evaluaciones$ = this.createObservable().pipe(
      switchMap((response) => {
        // Map response total
        this.totalElementos = response.total;
        // Reset pagination to first page
        if (reset) {
          this.paginator.pageIndex = 0;
        }

        if (response.items) {
          // Solicitantes
          const listObservables: Observable<IEvaluacion>[] = [];
          response.items.forEach((evaluacion) => {
            const evaluacion$ = this.personaService.findById(
              evaluacion.memoria?.peticionEvaluacion?.solicitante?.id
            ).pipe(
              map((personaInfo) => {
                evaluacion.memoria.peticionEvaluacion.solicitante = personaInfo;
                return evaluacion;
              })
            );
            listObservables.push(evaluacion$);
          });

          return zip(...listObservables);
        } else {
          return of([]);
        }
      }),
    ),
      catchError((error) => {
        this.logger.error(error);
        // On error reset pagination values
        this.paginator.firstPage();
        this.totalElementos = 0;
        this.snackBarService.showError(MSG_ERROR);
        return of([]);
      });
  }

  /**
   * Devuelve el nombre de un comité.
   * @param comite comité
   * returns nombre comité
   */
  getComite(comite: IComite): string {
    return comite?.comite;
  }

  /**
   * Devuelve el nombre de un tipo evaluacion.
   * @param tipoEvaluacion tipo de evaluación
   * @returns nombre de un tipo de evaluación
   */
  getTipoEvaluacion(tipoEvaluacion: TipoEvaluacion): string {
    return tipoEvaluacion?.nombre;
  }

  /**
   * Devuelve el nombre de un tipo convocatoria reunión.
   * @param convocatoria tipo convocatoria reunión.
   * returns nombre tipo convocatoria reunión.
   */
  getTipoConvocatoriaReunion(convocatoria: TipoConvocatoriaReunion): string {
    return convocatoria?.nombre;
  }

  /**
   * Recupera un listado de los comités que hay en el sistema.
   */
  loadComites(): void {
    this.suscripciones.push(this.comiteService.findAll().subscribe(
      (response) => {
        this.comiteListado = response.items;

        this.filteredComites = this.formGroup.controls.comite.valueChanges
          .pipe(
            startWith(''),
            map(value => this.filterComite(value))
          );
      }));
  }

  /**
   * Recupera un listado de los tipos de evaluacion que hay en el sistema.
   */
  loadTipoEvaluacion(): void {
    this.suscripciones.push(this.tipoEvaluacionService.findTipoEvaluacionSeguimientoAnualFinal().subscribe(
      (response) => {
        this.tipoEvaluacionListado = response.items;

        this.filteredTipoEvaluacion = this.formGroup.controls.tipoEvaluacion.valueChanges
          .pipe(
            startWith(''),
            map(value => this.filterTipoEvaluacion(value))
          );
      }));
  }

  /**
   * Recupera un listado de los tipos convocatoria que hay en el sistema.
   */
  loadConvocatoriasReunion(): void {
    this.suscripciones.push(this.tipoConvocatoriaReunionService.findAll().subscribe(
      (response) => {
        this.tipoConvocatoriaReunionListado = response.items;

        this.filteredTipoConvocatoriaReunion = this.formGroup.controls.tipoConvocatoriaReunion.valueChanges
          .pipe(
            startWith(''),
            map(value => this.filterTipoConvocatoriaReunion(value))
          );
      },
      (error) => {
        this.logger.error(error);
        this.snackBarService.showError(MSG_ERROR);
      }
    ));
  }

  /**
   * Filtro de campo autocompletable comité.
   * @param value value a filtrar (string o nombre comité).
   * @returns lista de comités filtrados.
   */
  private filterComite(value: string | IComite): IComite[] {
    let filterValue: string;
    if (typeof value === 'string') {
      filterValue = value.toLowerCase();
    } else {
      filterValue = value.comite.toLowerCase();
    }

    return this.comiteListado.filter
      (comite => comite.comite.toLowerCase().includes(filterValue));
  }

  /**
   * Filtro de campo autocompletable tipo evaluación.
   * @param value value a filtrar (string o nombre tipo evaluación).
   * @returns lista de tipo evaluación filtrados.
   */
  private filterTipoEvaluacion(value: string | TipoEvaluacion): TipoEvaluacion[] {
    let filterValue: string;
    if (typeof value === 'string') {
      filterValue = value.toLowerCase();
    } else {
      filterValue = value.nombre.toLowerCase();
    }

    return this.tipoEvaluacionListado.filter
      (tipoEvaluacion => tipoEvaluacion.nombre.toLowerCase().includes(filterValue));
  }

  /**
   * Filtro de campo autocompletable tipo convocatoria reunión.
   * @param value value a filtrar (string o nombre tipo convocatoria reunion).
   * @returns lista de tipos de convocatorias filtrados.
   */
  private filterTipoConvocatoriaReunion(value: string | TipoConvocatoriaReunion): TipoConvocatoriaReunion[] {
    let filterValue: string;
    if (typeof value === 'string') {
      filterValue = value.toLowerCase();
    } else {
      filterValue = value.nombre.toLowerCase();
    }

    return this.tipoConvocatoriaReunionListado.filter
      (tipoConvocatoriaReunion => tipoConvocatoriaReunion.nombre.toLowerCase().includes(filterValue));
  }

  /**
   * Clean filters an reload the table
   */
  onClearFilters(): void {
    super.onClearFilters();
    this.formGroup.controls.fechaEvaluacionInicio.setValue(null);
    this.formGroup.controls.fechaEvaluacionFin.setValue(null);
  }

}
