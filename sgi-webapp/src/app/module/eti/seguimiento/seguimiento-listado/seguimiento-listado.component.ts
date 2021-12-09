import { Component, OnInit } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { AbstractTablePaginationComponent } from '@core/component/abstract-table-pagination.component';
import { IComite } from '@core/models/eti/comite';
import { IEvaluacion } from '@core/models/eti/evaluacion';
import { TipoEvaluacion } from '@core/models/eti/tipo-evaluacion';
import { IPersona } from '@core/models/sgp/persona';
import { FxFlexProperties } from '@core/models/shared/flexLayout/fx-flex-properties';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { ComiteService } from '@core/services/eti/comite.service';
import { EvaluadorService } from '@core/services/eti/evaluador.service';
import { TipoEvaluacionService } from '@core/services/eti/tipo-evaluacion.service';
import { PersonaService } from '@core/services/sgp/persona.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { RSQLSgiRestFilter, SgiRestFilter, SgiRestFilterOperator, SgiRestListResult } from '@sgi/framework/http';
import { NGXLogger } from 'ngx-logger';
import { Observable } from 'rxjs';
import { map, startWith } from 'rxjs/operators';

const MSG_ERROR = marker('error.load');

@Component({
  selector: 'sgi-seguimiento-listado',
  templateUrl: './seguimiento-listado.component.html',
  styleUrls: ['./seguimiento-listado.component.scss']
})
export class SeguimientoListadoComponent extends AbstractTablePaginationComponent<IEvaluacion> implements OnInit {
  evaluaciones: IEvaluacion[];
  fxFlexProperties: FxFlexProperties;
  fxLayoutProperties: FxLayoutProperties;
  comiteListado: IComite[];
  comitesFiltrados: Observable<IComite[]>;
  tipoEvaluacionListado: TipoEvaluacion[];
  tipoEvaluacionFiltrados: Observable<TipoEvaluacion[]>;

  constructor(
    private readonly logger: NGXLogger,
    protected readonly snackBarService: SnackBarService,
    private readonly personaService: PersonaService,
    private readonly comiteService: ComiteService,
    private readonly tipoEvaluacionService: TipoEvaluacionService,
    private readonly evaluadorService: EvaluadorService
  ) {
    super(snackBarService, MSG_ERROR);
    this.fxFlexProperties = new FxFlexProperties();
    this.fxFlexProperties.sm = '0 1 calc(50%-10px)';
    this.fxFlexProperties.md = '0 1 calc(33%-10px)';
    this.fxFlexProperties.gtMd = '0 1 calc(22%-10px)';
    this.fxFlexProperties.order = '2';

    this.fxLayoutProperties = new FxLayoutProperties();
    this.fxLayoutProperties.gap = '20px';
    this.fxLayoutProperties.layout = 'row wrap';
    this.fxLayoutProperties.xs = 'column';
  }

  ngOnInit() {
    super.ngOnInit();
    this.formGroup = new FormGroup({
      comite: new FormControl(''),
      fechaEvaluacionInicio: new FormControl(null),
      fechaEvaluacionFin: new FormControl(null),
      memoriaNumReferencia: new FormControl(''),
      tipoConvocatoria: new FormControl(''),
      tipoEvaluacion: new FormControl('')
    });
    this.loadComites();
    this.loadTipoEvaluacion();
  }

  onClearFilters(): void {
    super.onClearFilters();
    this.formGroup.controls.fechaEvaluacionInicio.setValue(null);
    this.formGroup.controls.fechaEvaluacionFin.setValue(null);
  }

  protected createObservable(reset?: boolean): Observable<SgiRestListResult<IEvaluacion>> {
    const observable$ = this.evaluadorService.getSeguimientos(this.getFindOptions(reset));
    return observable$;
  }

  protected initColumns(): void {
    this.columnas = ['memoria.comite.comite', 'tipoEvaluacion.nombre', 'convocatoriaReunion.fechaEvaluacion',
      'memoria.numReferencia', 'solicitante', 'version', 'acciones'];
  }

  protected loadTable(reset?: boolean): void {
    const evaluaciones$ = this.getObservableLoadTable(reset);
    this.suscripciones.push(
      evaluaciones$.subscribe(
        (evaluaciones) => {
          this.evaluaciones = [];
          if (evaluaciones) {
            this.evaluaciones = evaluaciones;
            this.loadSolicitantes();
          }
        },
        (error) => {
          this.logger.error(error);
          this.snackBarService.showError(MSG_ERROR);
        })
    );
  }

  /**
   * Carga los datos de los solicitantes de las evaluaciones
   */
  private loadSolicitantes(): void {
    this.evaluaciones.map((evaluacion) => {
      const personaRef = evaluacion.memoria?.peticionEvaluacion?.solicitante?.id;
      if (personaRef) {
        this.suscripciones.push(
          this.personaService.findById(personaRef).subscribe(
            (persona: IPersona) => evaluacion.memoria.peticionEvaluacion.solicitante = persona
          )
        );
      }
    });
  }

  protected createFilter(): SgiRestFilter {
    const controls = this.formGroup.controls;
    const filter = new RSQLSgiRestFilter('memoria.comite.id', SgiRestFilterOperator.EQUALS, controls.comite.value?.id?.toString())
      .and(
        'convocatoriaReunion.fechaEvaluacion',
        SgiRestFilterOperator.GREATHER_OR_EQUAL,
        LuxonUtils.toBackend(controls.fechaEvaluacionInicio.value)
      ).and(
        'convocatoriaReunion.fechaEvaluacion',
        SgiRestFilterOperator.LOWER_OR_EQUAL,
        LuxonUtils.toBackend(controls.fechaEvaluacionFin.value)
      ).and('memoria.numReferencia', SgiRestFilterOperator.LIKE_ICASE, controls.memoriaNumReferencia.value)
      .and('tipoEvaluacion.id', SgiRestFilterOperator.EQUALS, controls.tipoEvaluacion.value?.id?.toString());

    return filter;
  }

  /**
   * Carga todas los comites existentes
   */
  private loadComites(): void {
    this.suscripciones.push(
      this.comiteService.findAll().subscribe(
        (res: SgiRestListResult<IComite>) => {
          if (res) {
            this.comiteListado = res.items;
            this.comitesFiltrados = this.formGroup.controls.comite.valueChanges
              .pipe(
                startWith(''),
                map(valor => this.filterComites(valor))
              );
          } else {
            this.comiteListado = [];
          }
        })
    );
  }

  /**
   * Carga los tipos de evaluacion: Seguimiento Anual y Seguimiento Final
   */
  private loadTipoEvaluacion(): void {
    this.suscripciones.push(
      this.tipoEvaluacionService.findTipoEvaluacionSeguimientoAnualFinal().subscribe(
        (res: SgiRestListResult<TipoEvaluacion>) => {
          if (res) {
            this.tipoEvaluacionListado = res.items;
            this.tipoEvaluacionFiltrados = this.formGroup.controls.tipoEvaluacion.valueChanges
              .pipe(
                startWith(''),
                map(valor => this.filterTipoEvaluacion(valor))
              );
          } else {
            this.tipoEvaluacionListado = [];
          }
        })
    );
  }

  /**
   * Filtro de campo autocompletable comité.
   *
   * @param filtro valor a filtrar (string o nombre comité).
   * @return lista de comités filtrados.
   */
  private filterComites(filtro: string | IComite): IComite[] {
    const valorLog = filtro instanceof String ? filtro : JSON.stringify(filtro);
    const result = this.comiteListado.filter(
      (comite: IComite) => comite.comite.toLowerCase().includes(
        typeof filtro === 'string' ? filtro.toLowerCase() : filtro.comite.toLowerCase()
      )
    );
    return result;
  }

  /**
   * Filtro de campo autocompletable tipo evaluacion.
   *
   * @param filtro valor a filtrar (string o nombre tipo evaluacion).
   * @return lista de tipo evaluacion filtrados.
   */
  private filterTipoEvaluacion(filtro: string | TipoEvaluacion): TipoEvaluacion[] {
    const result = this.tipoEvaluacionListado.filter(
      (tipoEvaluacion: TipoEvaluacion) => tipoEvaluacion.nombre.toLowerCase().includes(
        typeof filtro === 'string' ? filtro.toLowerCase() : filtro.nombre.toLowerCase()
      )
    );
    return result;
  }

  /**
   * Devuelve el nombre de un comité.
   * @param comite comité
   * @returns nombre comité
   */
  getNombreComite(comite: IComite): string {
    return comite?.comite;
  }

  /**
   * Devuelve el nombre de un tipo evaluacion.
   * @param tipoEvaluacion tipo de evaluación
   * @returns nombre de un tipo de evaluación
   */
  getNombreTipoEvaluacion(tipoEvaluacion: TipoEvaluacion): string {
    return tipoEvaluacion?.nombre;
  }
}
