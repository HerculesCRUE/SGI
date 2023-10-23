import { Component, OnInit, ViewChild } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { ActivatedRoute } from '@angular/router';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { AbstractTablePaginationComponent } from '@core/component/abstract-table-pagination.component';
import { IBaseExportModalData } from '@core/component/base-export/base-export-modal-data';
import { SgiError } from '@core/errors/sgi-error';
import { MSG_PARAMS } from '@core/i18n';
import { IActaWithNumEvaluaciones } from '@core/models/eti/acta-with-num-evaluaciones';
import { ESTADO_ACTA_MAP } from '@core/models/eti/tipo-estado-acta';
import { IDocumento } from '@core/models/sgdoc/documento';
import { FxFlexProperties } from '@core/models/shared/flexLayout/fx-flex-properties';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { Module } from '@core/module';
import { ROUTE_NAMES } from '@core/route.names';
import { ConfigService } from '@core/services/cnf/config.service';
import { DialogService } from '@core/services/dialog.service';
import { ActaService } from '@core/services/eti/acta.service';
import { EvaluacionService } from '@core/services/eti/evaluacion.service';
import { DocumentoService, triggerDownloadToUser } from '@core/services/sgdoc/documento.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { TranslateService } from '@ngx-translate/core';
import { SgiAuthService } from '@sgi/framework/auth';
import { RSQLSgiRestFilter, SgiRestFilter, SgiRestFilterOperator, SgiRestListResult } from '@sgi/framework/http';
import { NGXLogger } from 'ngx-logger';
import { Observable, of } from 'rxjs';
import { catchError, map, switchMap } from 'rxjs/operators';
import { ActaListadoExportModalComponent } from '../modals/acta-listado-export-modal/acta-listado-export-modal.component';

const MSG_BUTTON_NEW = marker('btn.add.entity');
const MSG_FINALIZAR_ERROR = marker('error.eti.acta.finalizar');
const MSG_FINALIZAR_SUCCESS = marker('msg.eti.acta.finalizar.success');
const ACTA_KEY = marker('eti.acta');
const MSG_REGISTRO_BLOCKCHAIN_OK = marker('msg.eti.acta.registro-blockchain.ok');
const MSG_REGISTRO_BLOCKCHAIN_ALTERADO = marker('msg.eti.acta.registro-blockchain.alterado');

export interface IActaWithNumEvaluacionesAndComentariosEnviados extends IActaWithNumEvaluaciones {
  enviada: boolean;
  permitirEnviarComentarios: boolean;
}

const MSG_ENVIAR_COMENTARIO = marker('msg.enviar.comentario');
const MSG_ENVIAR_COMENTARIO_SUCCESS = marker('msg.enviar.comentario.success');

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

  textoCrear: string;
  private textoFinalizarError: string;

  blockchainEnable: boolean;

  actas: IActaWithNumEvaluacionesAndComentariosEnviados[];

  private textoEnviarComentario: string;
  private textoEnviarComentarioSuccess: string;

  private usuarioRef: string;

  get ESTADO_ACTA_MAP() {
    return ESTADO_ACTA_MAP;
  }

  get showAddAndFinishActa(): boolean {
    return !this.isModuleInv;
  }

  get showActaComentariosEnviados(): boolean {
    return this.isRolEvaluador;
  }

  private limiteRegistrosExportacionExcel: string;
  private isModuleInv: boolean;
  private isRolEvaluador: boolean;

  constructor(
    private readonly logger: NGXLogger,
    private readonly actasService: ActaService,
    protected readonly snackBarService: SnackBarService,
    private readonly translate: TranslateService,
    private readonly documentoService: DocumentoService,
    private readonly matDialog: MatDialog,
    private readonly cnfService: ConfigService,
    private readonly route: ActivatedRoute,
    private readonly evaluacionService: EvaluacionService,
    private readonly dialogService: DialogService,
    private readonly authService: SgiAuthService
  ) {
    super();

    this.isModuleInv = route.snapshot.data.module === Module.INV;
    this.usuarioRef = this.authService.authStatus$.value.userRefId;
    this.isRolEvaluador = authService.hasAuthority('ETI-ACT-V') ? false : true;

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
      comite: new FormControl(null),
      fechaEvaluacionInicio: new FormControl(null, []),
      fechaEvaluacionFin: new FormControl(null, []),
      numeroActa: new FormControl('', []),
      tipoEstadoActa: new FormControl(null, [])
    });

    this.suscripciones.push(
      this.cnfService.getLimiteRegistrosExportacionExcel('eti-exp-max-num-registros-excel-acta-listado').subscribe(value => {
        this.limiteRegistrosExportacionExcel = value;
      }));

  }

  private setupI18N(): void {
    this.suscripciones.push(this.translate.get(
      ACTA_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_BUTTON_NEW,
          { entity: value }
        );
      })
    ).subscribe((value) => this.textoCrear = value));

    this.suscripciones.push(this.translate.get(
      MSG_FINALIZAR_ERROR
    ).subscribe((value) => this.textoFinalizarError = value));

    this.translate.get(
      MSG_ENVIAR_COMENTARIO
    ).subscribe((value) => this.textoEnviarComentario = value);

    this.translate.get(
      MSG_ENVIAR_COMENTARIO_SUCCESS
    ).subscribe((value) => this.textoEnviarComentarioSuccess = value);
  }

  protected createObservable(reset?: boolean): Observable<SgiRestListResult<IActaWithNumEvaluaciones>> {
    const observable$ = this.actasService.findActivasWithEvaluaciones(this.getFindOptions(reset));
    this.suscripciones.push(this.cnfService.isBlockchainEnable().subscribe(value => {
      this.blockchainEnable = value;
    }));
    return observable$;
  }

  protected initColumns(): void {
    if (this.isRolEvaluador) {
      this.displayedColumns = ['convocatoriaReunion.comite', 'convocatoriaReunion.fechaEvaluacion', 'numero', 'convocatoriaReunion.tipoConvocatoriaReunion',
        'numeroIniciales', 'numeroRevisiones', 'numeroTotal', 'estadoActual.nombre', 'comentariosEnviados', 'acciones'];
    } else {
      this.displayedColumns = ['convocatoriaReunion.comite', 'convocatoriaReunion.fechaEvaluacion', 'numero', 'convocatoriaReunion.tipoConvocatoriaReunion',
        'numeroIniciales', 'numeroRevisiones', 'numeroTotal', 'estadoActual.nombre', 'acciones'];
    }
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
      controls.tipoEstadoActa.value?.toString()
    );

    return filter;
  }

  protected resetFilters(): void {
    super.resetFilters();
    this.formGroup.controls.fechaEvaluacionInicio.setValue(null);
    this.formGroup.controls.fechaEvaluacionFin.setValue(null);
  }

  protected loadTable(reset?: boolean) {
    this.actas$ = this.getObservableLoadTable(reset);
    this.suscripciones.push(
      this.actas$.subscribe(
        (actas: IActaWithNumEvaluaciones[]) => {
          if (actas) {
            this.actas = actas as IActaWithNumEvaluacionesAndComentariosEnviados[];
            this.loadActaWithComentariosEnviados();
            this.loadExistsActaWithComentarioAbiertos();
          } else {
            this.actas = [];
          }
        },
        (error) => {
          this.processError(error);
        })
    );
  }

  private loadActaWithComentariosEnviados(): void {
    this.actas.forEach((acta) => {
      this.suscripciones.push(
        this.actasService.isComentariosEnviados(acta.id).subscribe(
          (res: boolean) => {
            acta.enviada = res;
          },
          (error) => {
            this.processError(error);
          }
        )
      );
    })
  }

  private loadExistsActaWithComentarioAbiertos(): void {
    this.actas.forEach((acta) => {
      this.suscripciones.push(
        this.actasService.isPosibleEnviarComentarios(acta.id).subscribe(
          (res: boolean) => {
            acta.permitirEnviarComentarios = res;
          },
          (error) => {
            this.processError(error);
          }
        )
      );
    })
  }

  /**
   * Finaliza el acta con el id recibido.
   * @param actaId id del acta a finalizar.
   */
  finishActa(actaId: number) {
    this.suscripciones.push(
      this.actasService.finishActa(actaId).subscribe(() => {
        this.snackBarService.showSuccess(MSG_FINALIZAR_SUCCESS);
        this.loadTable(false);
      },
        (error) => {
          this.logger.error(error);
          // On error reset pagination values
          if (error instanceof SgiError) {
            this.processError(error);
          }
          else {
            this.processError(new SgiError(this.textoFinalizarError));
          }
          return of([]);
        })
    );
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
  visualizarInforme(acta: IActaWithNumEvaluaciones): void {
    const documento: IDocumento = {} as IDocumento;
    if (this.isFinalizada(acta)) {
      this.suscripciones.push(this.documentoService.getInfoFichero(acta.documentoRef).pipe(
        switchMap((documentoInfo: IDocumento) => {
          documento.nombre = documentoInfo.nombre;
          return this.documentoService.downloadFichero(acta.documentoRef);
        })
      ).subscribe(response => {
        triggerDownloadToUser(response, documento.nombre);
      }));
    } else {
      this.suscripciones.push(this.actasService.getDocumentoActa(acta.id).pipe(
        switchMap((documentoInfo: IDocumento) => {
          documento.nombre = documentoInfo.nombre;
          return this.documentoService.downloadFichero(documentoInfo.documentoRef);
        })
      ).subscribe(
        (response) => {
          triggerDownloadToUser(response, documento.nombre);
        },
        this.processError
      ));
    }
  }

  openExportModal(): void {
    const data: IBaseExportModalData = {
      findOptions: this.findOptions,
      totalRegistrosExportacionExcel: this.totalElementos,
      limiteRegistrosExportacionExcel: Number(this.limiteRegistrosExportacionExcel)
    };

    const config = {
      data
    };
    this.matDialog.open(ActaListadoExportModalComponent, config);
  }

  /**
   * Confirma el registro blockchain
   * @param actaId id del acta a confirmar.
   */
  confirmarRegistroBlockchain(actaId: number) {
    this.suscripciones.push(
      this.actasService.isRegistroBlockchainConfirmado(actaId).subscribe(
        (value) => {
          if (value) {
            this.snackBarService.showSuccess(MSG_REGISTRO_BLOCKCHAIN_OK);
          } else {
            this.snackBarService.showSuccess(MSG_REGISTRO_BLOCKCHAIN_ALTERADO);
          }
        },
        this.processError
      )
    );
  }

  public enviarComentarios(idActa: number) {
    const enviarComentariosDialogSubscription = this.dialogService.showConfirmation(this.textoEnviarComentario).subscribe(
      (aceptado: boolean) => {
        if (aceptado) {
          const enviarComentariosSubscription = this.actasService
            .enviarComentarios(idActa)
            .pipe(
              map(() => {
                return this.loadTable();
              })
            ).subscribe(() => {
              this.snackBarService.showSuccess(this.textoEnviarComentarioSuccess);
            });
          this.suscripciones.push(enviarComentariosSubscription);
        }
        aceptado = false;
      });
    this.suscripciones.push(enviarComentariosDialogSubscription);
  }

}
