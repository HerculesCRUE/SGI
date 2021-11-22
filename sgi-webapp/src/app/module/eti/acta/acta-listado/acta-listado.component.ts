import { Component, OnInit, ViewChild } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { AbstractTablePaginationComponent } from '@core/component/abstract-table-pagination.component';
import { HttpProblem } from '@core/errors/http-problem';
import { MSG_PARAMS } from '@core/i18n';
import { IActaWithNumEvaluaciones } from '@core/models/eti/acta-with-num-evaluaciones';
import { IComite } from '@core/models/eti/comite';
import { TipoEstadoActa } from '@core/models/eti/tipo-estado-acta';
import { IDocumento } from '@core/models/sgdoc/documento';
import { FxFlexProperties } from '@core/models/shared/flexLayout/fx-flex-properties';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { ROUTE_NAMES } from '@core/route.names';
import { ActaService } from '@core/services/eti/acta.service';
import { ComiteService } from '@core/services/eti/comite.service';
import { TipoEstadoActaService } from '@core/services/eti/tipo-estado-acta.service';
import { DocumentoService, triggerDownloadToUser } from '@core/services/sgdoc/documento.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { TranslateService } from '@ngx-translate/core';
import { RSQLSgiRestFilter, SgiRestFilter, SgiRestFilterOperator, SgiRestListResult } from '@sgi/framework/http';
import { NGXLogger } from 'ngx-logger';
import { Observable, of, Subscription } from 'rxjs';
import { catchError, map, startWith, switchMap } from 'rxjs/operators';

const MSG_BUTTON_NEW = marker('btn.add.entity');
const MSG_ERROR = marker('error.load');
const MSG_FINALIZAR_ERROR = marker('error.eti.acta.finalizar');
const MSG_FINALIZAR_SUCCESS = marker('msg.eti.acta.finalizar.success');
const ACTA_KEY = marker('eti.acta');

@Component({
  selector: 'sgi-acta-listado',
  templateUrl: './acta-listado.component.html',
  styleUrls: ['./acta-listado.component.scss']
})
export class ActaListadoComponent extends AbstractTablePaginationComponent<IActaWithNumEvaluaciones> implements OnInit {

  ROUTE_NAMES = ROUTE_NAMES;

  fxFlexProperties: FxFlexProperties;
  fxLayoutProperties: FxLayoutProperties;

  displayedColumns: string[];

  @ViewChild(MatSort, { static: true }) sort: MatSort;
  @ViewChild(MatPaginator, { static: false }) paginator: MatPaginator;

  actas$: Observable<IActaWithNumEvaluaciones[]> = of();

  comiteListado: IComite[];
  comitesSubscription: Subscription;
  filteredComites: Observable<IComite[]>;

  tipoEstadoActaListado: TipoEstadoActa[];
  tipoEstadoActaSubscription: Subscription;
  filteredTipoEstadoActa: Observable<TipoEstadoActa[]>;

  finalizarSubscription: Subscription;

  textoCrear: string;

  constructor(
    private readonly logger: NGXLogger,
    private readonly actasService: ActaService,
    protected readonly snackBarService: SnackBarService,
    private readonly comiteService: ComiteService,
    private readonly tipoEstadoActaService: TipoEstadoActaService,
    private readonly translate: TranslateService,
    private readonly documentoService: DocumentoService
  ) {

    super(snackBarService, MSG_ERROR);

    this.fxFlexProperties = new FxFlexProperties();
    this.fxFlexProperties.sm = '0 1 calc(50%-10px)';
    this.fxFlexProperties.md = '0 1 calc(33%-10px)';
    this.fxFlexProperties.gtMd = '0 1 calc(17%-10px)';
    this.fxFlexProperties.order = '2';

    this.fxLayoutProperties = new FxLayoutProperties();
    this.fxLayoutProperties.gap = '20px';
    this.fxLayoutProperties.layout = 'row wrap';
    this.fxLayoutProperties.xs = 'column';
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.setupI18N();

    this.formGroup = new FormGroup({
      comite: new FormControl('', []),
      fechaEvaluacionInicio: new FormControl(null, []),
      fechaEvaluacionFin: new FormControl(null, []),
      numeroActa: new FormControl('', []),
      tipoEstadoActa: new FormControl('', [])
    });

    this.getComites();

    this.getTipoEstadoActas();
  }

  private setupI18N(): void {
    this.translate.get(
      ACTA_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_BUTTON_NEW,
          { entity: value }
        );
      })
    ).subscribe((value) => this.textoCrear = value);
  }

  protected createObservable(): Observable<SgiRestListResult<IActaWithNumEvaluaciones>> {
    const observable$ = this.actasService.findActivasWithEvaluaciones(this.getFindOptions());
    return observable$;
  }

  protected initColumns(): void {
    this.displayedColumns = ['convocatoriaReunion.comite', 'convocatoriaReunion.fechaEvaluacion', 'numero', 'convocatoriaReunion.tipoConvocatoriaReunion',
      'numeroIniciales', 'numeroRevisiones', 'numeroTotal', 'estadoActual.nombre', 'acciones'];
  }

  protected createFilter(): SgiRestFilter {
    const controls = this.formGroup.controls;
    const filter = new RSQLSgiRestFilter(
      'convocatoriaReunion.comite.id',
      SgiRestFilterOperator.EQUALS,
      controls.comite.value?.id?.toString()
    ).and(
      'convocatoriaReunion.fechaEvaluacion',
      SgiRestFilterOperator.GREATHER_OR_EQUAL,
      LuxonUtils.toBackend(controls.fechaEvaluacionInicio.value)
    ).and(
      'convocatoriaReunion.fechaEvaluacion',
      SgiRestFilterOperator.LOWER_OR_EQUAL,
      LuxonUtils.toBackend(controls.fechaEvaluacionFin.value)
    ).and(
      'convocatoriaReunion.numeroActa',
      SgiRestFilterOperator.EQUALS,
      controls.numeroActa?.value.toString()
    ).and(
      'estadoActual.id',
      SgiRestFilterOperator.EQUALS,
      controls.tipoEstadoActa.value?.id?.toString()
    );

    return filter;
  }

  onClearFilters(): void {
    super.onClearFilters();
    this.formGroup.controls.fechaEvaluacionInicio.setValue(null);
    this.formGroup.controls.fechaEvaluacionFin.setValue(null);
  }

  protected loadTable(reset?: boolean) {
    this.actas$ = this.getObservableLoadTable(reset);
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
   * Devuelve el nombre de un tipo estado acta.
   * @param estado tipo estado acta
   * returns nombre tipo estado acta
   */
  getTipoEstadoActa(estado: TipoEstadoActa): string {
    return estado?.nombre;
  }

  /**
   * Recupera un listado de los comités que hay en el sistema.
   */
  getComites(): void {
    this.comitesSubscription = this.comiteService.findAll().subscribe(
      (response) => {
        this.comiteListado = response.items;

        this.filteredComites = this.formGroup.controls.comite.valueChanges
          .pipe(
            startWith(''),
            map(value => this._filterComite(value))
          );
      });
  }

  /**
   * Recupera un listado de los tipos de estados de acta que hay en el sistema.
   */
  getTipoEstadoActas(): void {
    this.tipoEstadoActaSubscription = this.tipoEstadoActaService.findAll().subscribe(
      (response) => {
        this.tipoEstadoActaListado = response.items;

        this.filteredTipoEstadoActa = this.formGroup.controls.tipoEstadoActa.valueChanges
          .pipe(
            startWith(''),
            map(value => this._filterTipoEstado(value))
          );
      });
  }

  /**
   * Filtro de campo autocompletable comité.
   * @param value value a filtrar (string o nombre comité).
   * @returns lista de comités filtrados.
   */
  private _filterComite(value: string | IComite): IComite[] {
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
   * Filtro de campo autocompletable tipo estado acta.
   * @param value value a filtrar (string o nombre tipo estado acta).
   * @returns lista de tipos de estados filtrados.
   */
  private _filterTipoEstado(value: string | TipoEstadoActa): TipoEstadoActa[] {
    let filterValue: string;
    if (typeof value === 'string') {
      filterValue = value.toLowerCase();
    } else {
      filterValue = value.nombre.toLowerCase();
    }

    return this.tipoEstadoActaListado.filter
      (tipoEstadoActa => tipoEstadoActa.nombre.toLowerCase().includes(filterValue));
  }

  /**
   * Finaliza el acta con el id recibido.
   * @param actaId id del acta a finalizar.
   */
  finishActa(actaId: number) {
    this.finalizarSubscription = this.actasService.finishActa(actaId).subscribe((acta) => {
      this.snackBarService.showSuccess(MSG_FINALIZAR_SUCCESS);
      this.loadTable(false);
    },
      catchError((error) => {
        this.logger.error(error);
        // On error reset pagination values
        this.paginator.firstPage();
        this.totalElementos = 0;
        if (error instanceof HttpProblem) {
          this.snackBarService.showError(error);
        }
        else {
          this.snackBarService.showError(MSG_FINALIZAR_ERROR);
        }
        return of([]);
      }));
  }

  /**
   * Comprueba si una acta se encuentra en estado finalizada.
   * @param acta acta a comprobar.
   * @return indicador de si el acta se encuentra finalizada.
   */
  isFinalizada(acta: IActaWithNumEvaluaciones): boolean {
    return acta.estadoActa?.id === 2;
  }

  /**
   * Comprueba si una acta puede ser finalizado.
   * @param acta acta a comprobar.
   * @return indicador de si se puede finalizar el acta.
   */
  hasFinalizarActa(acta: IActaWithNumEvaluaciones): boolean {
    return acta.estadoActa.id === 1 && acta.numEvaluacionesNoEvaluadas === 0;
  }

  /**
   * Visualiza el informe seleccionado.
   * @param documentoRef Referencia del informe..
   */
  visualizarInforme(idActa: number): void {
    const documento: IDocumento = {} as IDocumento;
    this.actasService.getDocumentoActa(idActa).pipe(
      switchMap((documentoInfo: IDocumento) => {
        documento.nombre = documentoInfo.nombre;
        return this.documentoService.downloadFichero(documentoInfo.documentoRef);
      })
    ).subscribe(response => {
      triggerDownloadToUser(response, documento.nombre);
    });
  }

}
