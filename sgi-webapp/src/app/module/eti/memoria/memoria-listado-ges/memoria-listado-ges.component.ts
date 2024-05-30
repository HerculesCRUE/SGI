import { Component, OnInit, ViewChild } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { AbstractTablePaginationComponent } from '@core/component/abstract-table-pagination.component';
import { SgiError } from '@core/errors/sgi-error';
import { MSG_PARAMS } from '@core/i18n';
import { IMemoria } from '@core/models/eti/memoria';
import { IMemoriaPeticionEvaluacion } from '@core/models/eti/memoria-peticion-evaluacion';
import { ESTADO_MEMORIA, ESTADO_MEMORIA_MAP } from '@core/models/eti/tipo-estado-memoria';
import { IPersona } from '@core/models/sgp/persona';
import { FxFlexProperties } from '@core/models/shared/flexLayout/fx-flex-properties';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { ROUTE_NAMES } from '@core/route.names';
import { ConfigService } from '@core/services/cnf/config.service';
import { DialogService } from '@core/services/dialog.service';
import { MemoriaService } from '@core/services/eti/memoria.service';
import { PersonaService } from '@core/services/sgp/persona.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { TranslateService } from '@ngx-translate/core';
import { RSQLSgiRestFilter, SgiRestFilter, SgiRestFilterOperator, SgiRestListResult } from '@sgi/framework/http';
import { NGXLogger } from 'ngx-logger';
import { Observable, of } from 'rxjs';
import { catchError, filter, map, switchMap } from 'rxjs/operators';
import { TipoColectivo } from 'src/app/esb/sgp/shared/select-persona/select-persona.component';
import { MEMORIAS_ROUTE } from '../memoria-route-names';
import { IMemoriaListadoModalData, MemoriaListadoExportModalComponent } from '../modals/memoria-listado-export-modal/memoria-listado-export-modal.component';

const MSG_BUTTON_SAVE = marker('btn.add.entity');
const MSG_ESTADO_ANTERIOR_OK = marker('msg.eti.memoria.estado-anterior.success');
const MSG_ESTADO_ANTERIOR_ERROR = marker('error.eti.memoria.estado-anterior');
const MSG_RECUPERAR_ESTADO = marker('msg.eti.memoria.estado-anterior');
const PETICION_EVALUACION_KEY = marker('eti.peticion-evaluacion-etica-proyecto');
const MSG_SUCCESS_NOTIFICAR_REV_MINIMA = marker('msg.eti.memoria.notificar-revision-minima.success');
const MSG_ERROR_NOTIFICAR_REV_MINIMA = marker('msg.eti.memoria.notificar-revision-minima.error');
const MSG_CONFIRMACION_NOTIFICAR_REV_MINIMA = marker('msg.eti.memoria.notificar-rev-minima.confirmacion');

@Component({
  selector: 'sgi-memoria-listado-ges',
  templateUrl: './memoria-listado-ges.component.html',
  styleUrls: ['./memoria-listado-ges.component.scss']
})
export class MemoriaListadoGesComponent extends AbstractTablePaginationComponent<IMemoriaPeticionEvaluacion> implements OnInit {
  MEMORIAS_ROUTE = MEMORIAS_ROUTE;
  ROUTE_NAMES = ROUTE_NAMES;

  fxFlexProperties: FxFlexProperties;
  fxLayoutProperties: FxLayoutProperties;

  displayedColumns: string[];
  totalElementos: number;

  textoCrear: string;
  textoNotificarRevMinimaSuccess: string;
  textoNotificarRevMinimaError: string;

  @ViewChild(MatSort, { static: true }) sort: MatSort;
  @ViewChild(MatPaginator, { static: false }) paginator: MatPaginator;

  memorias$: Observable<IMemoriaPeticionEvaluacion[]>;

  private limiteRegistrosExportacionExcel: string;

  get tipoColectivoSolicitante() {
    return TipoColectivo.SOLICITANTE_ETICA;
  }

  get ESTADO_MEMORIA_MAP() {
    return ESTADO_MEMORIA_MAP;
  }

  constructor(
    private readonly logger: NGXLogger,
    protected readonly snackBarService: SnackBarService,
    private readonly dialogService: DialogService,
    private readonly memoriaService: MemoriaService,
    private readonly translate: TranslateService,
    private readonly personaService: PersonaService,
    private matDialog: MatDialog,
    private readonly cnfService: ConfigService,
  ) {
    super();

    this.totalElementos = 0;

    this.suscripciones = [];

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

  ngOnInit(): void {
    super.ngOnInit();
    this.setupI18N();

    this.formGroup = new FormGroup({
      comite: new FormControl(null, []),
      titulo: new FormControl('', []),
      numReferencia: new FormControl('', []),
      tipoEstadoMemoria: new FormControl(null, []),
      solicitante: new FormControl('', []),
      texto: new FormControl(null, []),
    });

    this.suscripciones.push(
      this.cnfService.getLimiteRegistrosExportacionExcel('eti-exp-max-num-registros-excel-memoria-listado').subscribe(value => {
        this.limiteRegistrosExportacionExcel = value;
      }));
  }

  private setupI18N(): void {
    this.translate.get(
      PETICION_EVALUACION_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_BUTTON_SAVE,
          { entity: value, ...MSG_PARAMS.GENDER.FEMALE }
        );
      })
    ).subscribe((value) => this.textoCrear = value);

    this.translate.get(
      MSG_SUCCESS_NOTIFICAR_REV_MINIMA,
    ).subscribe((value) => this.textoNotificarRevMinimaSuccess = value);

    this.translate.get(
      MSG_ERROR_NOTIFICAR_REV_MINIMA
    ).subscribe((value) => this.textoNotificarRevMinimaError = value);
  }

  protected createObservable(reset?: boolean): Observable<SgiRestListResult<IMemoriaPeticionEvaluacion>> {
    const observable$ = this.memoriaService.findAll(this.getFindOptions(reset));
    // TODO: Eliminar casteo cuando se solucion la respuesta del back
    return observable$ as unknown as Observable<SgiRestListResult<IMemoriaPeticionEvaluacion>>;
  }

  /**
   * Devuelve los datos rellenos de los responsabes de las memorias
   * @param memorias el listado de memorias
   * returns los responsables de memorias con todos sus datos
   */
  getDatosResponsablesMemorias(memorias: IMemoriaPeticionEvaluacion[]): IMemoriaPeticionEvaluacion[] {
    memorias.forEach((memoria) => {
      // cambiar en futuro pasando las referencias de las personas
      memoria = this.loadDatosResponsable(memoria);
    });
    return memorias;
  }

  /**
   * Devuelve los datos de persona de la memoria
   * @param memoria la memoria
   * returns la memoria con los datos del responsable
   */
  loadDatosResponsable(memoria: IMemoriaPeticionEvaluacion): IMemoriaPeticionEvaluacion {
    const personaServiceOneSubscription = this.personaService.findById(memoria.solicitante.id)
      .subscribe(
        (persona: IPersona) => {
          memoria.solicitante = persona;
        },
        (error) => {
          this.logger.error(error);
          this.processError(error);
        }
      );
    this.suscripciones.push(personaServiceOneSubscription);
    return memoria;
  }

  protected initColumns(): void {
    this.displayedColumns = ['nombre', 'numReferencia', 'comite', 'estadoActual', 'fechaEvaluacion', 'fechaLimite', 'acciones'];
  }

  protected createFilter(): SgiRestFilter {
    const controls = this.formGroup.controls;
    return new RSQLSgiRestFilter('comite.id', SgiRestFilterOperator.EQUALS, controls.comite.value?.id?.toString())
      .and('peticionEvaluacion.titulo', SgiRestFilterOperator.LIKE_ICASE, controls.titulo.value)
      .and('numReferencia', SgiRestFilterOperator.LIKE_ICASE, controls.numReferencia.value)
      .and('estadoActual.id', SgiRestFilterOperator.EQUALS, controls.tipoEstadoMemoria.value?.toString())
      .and('peticionEvaluacion.personaRef', SgiRestFilterOperator.EQUALS, controls.solicitante.value.id)
      .and('textoContenidoRespuestaFormulario', SgiRestFilterOperator.LIKE_ICASE, this.formGroup.controls.texto.value);
  }

  protected loadTable(reset?: boolean) {
    this.memorias$ = this.getObservableLoadTable(reset).pipe(
      map((response) => {
        // Reset pagination to first page
        if (reset) {
          this.paginator.pageIndex = 0;
        }
        // Return the values
        return this.getDatosResponsablesMemorias(response as unknown as IMemoriaPeticionEvaluacion[]);
      }),
      catchError((error) => {
        this.logger.error(error);
        // On error reset pagination values
        this.paginator.firstPage();
        this.processError(error);
        return of([]);
      })
    );
  }

  /**
   * Se recupera el estado anterior de la memoria
   * @param memoria la memoria a reestablecer el estado
   */
  private recuperarEstadoAnteriorMemoria(memoria: IMemoriaPeticionEvaluacion) {
    const recuperarEstadoSubscription = this.memoriaService.recuperarEstadoAnterior(memoria.id).pipe(
      map((response: IMemoria) => {
        if (response) {
          // Si todo ha salido bien se recarga la tabla con el cambio de estado actualizado y el aviso correspondiente
          this.snackBarService.showSuccess(MSG_ESTADO_ANTERIOR_OK);
          this.loadTable(true);
        } else {
          // Si no se puede cambiar de estado se muestra el aviso
          this.snackBarService.showError(MSG_ESTADO_ANTERIOR_ERROR);
        }
      })
    ).subscribe();

    this.suscripciones.push(recuperarEstadoSubscription);
  }

  /**
   * Confirmación para recuperar el estado de la memoria
   * @param memoria la memoria a reestablecer el estado
   */
  public recuperarEstadoAnterior(memoria: IMemoriaPeticionEvaluacion) {
    const dialogServiceSubscription = this.dialogService.showConfirmation(MSG_RECUPERAR_ESTADO).pipe(
      map((aceptado: boolean) => {
        if (aceptado) {
          this.recuperarEstadoAnteriorMemoria(memoria);
        }
      })
    ).subscribe();

    this.suscripciones.push(dialogServiceSubscription);
  }

  public openExportModal() {
    const data: IMemoriaListadoModalData = {
      findOptions: this.findOptions,
      isInvestigador: false,
      totalRegistrosExportacionExcel: this.totalElementos,
      limiteRegistrosExportacionExcel: Number(this.limiteRegistrosExportacionExcel)
    };

    const config = {
      data
    };
    this.matDialog.open(MemoriaListadoExportModalComponent, config);
  }

  public showRecuperarEstadoAnterior(memoria: IMemoriaPeticionEvaluacion): boolean {
    return [
      ESTADO_MEMORIA.EN_SECRETARIA,
      ESTADO_MEMORIA.EN_SECRETARIA_REVISION_MINIMA,
      ESTADO_MEMORIA.EN_EVALUACION,
      ESTADO_MEMORIA.ARCHIVADA,
      ESTADO_MEMORIA.EN_EVALUACION_REVISION_MINIMA
    ].includes(memoria.estadoActual.id);
  }

  public showNotificarRevisionMinima(memoria: IMemoriaPeticionEvaluacion): boolean {
    return ESTADO_MEMORIA.EN_SECRETARIA_REVISION_MINIMA === memoria.estadoActual.id;
  }

  public notificarRevisionMinima(memoria: IMemoriaPeticionEvaluacion): void {
    this.suscripciones.push(
      this.dialogService.showConfirmation(MSG_CONFIRMACION_NOTIFICAR_REV_MINIMA).pipe(
        filter(aceptado => !!aceptado),
        switchMap(() => this.memoriaService.notificarRevisionMinima(memoria.id))
      ).subscribe(() => {
        this.snackBarService.showSuccess(this.textoNotificarRevMinimaSuccess);
        this.loadTable(true);
      },
        (error) => {
          if (error instanceof SgiError) {
            this.processError(error);
          } else {
            this.processError(new SgiError(this.textoNotificarRevMinimaError));
          }
        }
      )
    );
  }

}
