import { Component, OnInit, ViewChild } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { AbstractTablePaginationComponent } from '@core/component/abstract-table-pagination.component';
import { SgiError } from '@core/errors/sgi-error';
import { MSG_PARAMS } from '@core/i18n';
import { COMITE } from '@core/models/eti/comite';
import { ESTADO_RETROSPECTIVA } from '@core/models/eti/estado-retrospectiva';
import { IMemoria } from '@core/models/eti/memoria';
import { IMemoriaPeticionEvaluacion } from '@core/models/eti/memoria-peticion-evaluacion';
import { ESTADO_MEMORIA, ESTADO_MEMORIA_MAP } from '@core/models/eti/tipo-estado-memoria';
import { FxFlexProperties } from '@core/models/shared/flexLayout/fx-flex-properties';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { ROUTE_NAMES } from '@core/route.names';
import { ConfigService } from '@core/services/cnf/config.service';
import { DialogService } from '@core/services/dialog.service';
import { MemoriaService } from '@core/services/eti/memoria.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { TranslateService } from '@ngx-translate/core';
import { SgiAuthService } from '@sgi/framework/auth';
import { RSQLSgiRestFilter, SgiRestFilter, SgiRestFilterOperator, SgiRestListResult } from '@sgi/framework/http';
import { NGXLogger } from 'ngx-logger';
import { Observable, of } from 'rxjs';
import { map, switchMap } from 'rxjs/operators';
import { MEMORIAS_ROUTE } from '../memoria-route-names';
import { IMemoriaListadoModalData, MemoriaListadoExportModalComponent } from '../modals/memoria-listado-export-modal/memoria-listado-export-modal.component';

const MSG_BUTTON_SAVE = marker('btn.add.entity');
const MSG_SUCCESS_ENVIAR_SECRETARIA = marker('msg.eti.memoria.enviar-secretaria.success');
const MSG_ERROR_ENVIAR_SECRETARIA = marker('error.eti.memoria.enviar-secretaria');
const MSG_CONFIRM_ENVIAR_SECRETARIA = marker('msg.eti.memoria.enviar-secretaria');
const MSG_CONFIRM_ELIMINAR = marker('msg.delete.entity');
const MSG_SUCCESS_ENVIAR_SECRETARIA_RETROSPECTIVA = marker(
  'msg.eti.memoria.enviar-secretaria.retrospectiva.success');
const MSG_SUCCESS_ELIMINAR = marker('msg.delete.entity.success');
const MSG_ERROR_ELIMINAR = marker('error.delete.entity');
const MSG_ERROR_ENVIAR_SECRETARIA_RETROSPECTIVA = marker('error.eti.memoria.enviar-secretaria.retrospectiva');
const MSG_CONFIRM_ENVIAR_SECRETARIA_RETROSPECTIVA = marker('msg.eti.memoria.enviar-secretaria.retrospectiva');
const MEMORIA_KEY = marker('eti.memoria');
const PETICION_EVALUACION_KEY = marker('eti.peticion-evaluacion');
const MSG_ERROR_DATOS_ADJUNTOS = marker('error.eti.memoria.enviar-secretaria.documentos-adjuntos');

@Component({
  selector: 'sgi-memoria-listado-inv',
  templateUrl: './memoria-listado-inv.component.html',
  styleUrls: ['./memoria-listado-inv.component.scss']
})
export class MemoriaListadoInvComponent extends AbstractTablePaginationComponent<IMemoriaPeticionEvaluacion> implements OnInit {
  MEMORIAS_ROUTE = MEMORIAS_ROUTE;
  ROUTE_NAMES = ROUTE_NAMES;

  fxFlexProperties: FxFlexProperties;
  fxLayoutProperties: FxLayoutProperties;

  displayedColumns: string[];
  totalElementos: number;

  textoCrear: string;

  @ViewChild(MatSort, { static: true }) sort: MatSort;
  @ViewChild(MatPaginator, { static: false }) paginator: MatPaginator;

  memorias$: Observable<IMemoriaPeticionEvaluacion[]>;

  textoDelete: string;
  textoDeleteSuccess: string;
  textoDeleteError: string;

  private limiteRegistrosExportacionExcel: string;

  get ESTADO_MEMORIA_MAP() {
    return ESTADO_MEMORIA_MAP;
  }

  constructor(
    private readonly logger: NGXLogger,
    private readonly memoriaService: MemoriaService,
    protected readonly snackBarService: SnackBarService,
    protected readonly dialogService: DialogService,
    private readonly translate: TranslateService,
    private authService: SgiAuthService,
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
      tipoEstadoMemoria: new FormControl(null, [])
    });

    this.suscripciones.push(
      this.cnfService.getLimiteRegistrosExportacionExcel('eti-exp-max-num-registros-excel-memoria-listado').subscribe(value => {
        this.limiteRegistrosExportacionExcel = value;
      }));
  }

  private setupI18N(): void {
    this.translate.get(
      MEMORIA_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_CONFIRM_ELIMINAR,
          { entity: value, ...MSG_PARAMS.GENDER.FEMALE }
        );
      })
    ).subscribe((value) => this.textoDelete = value);

    this.translate.get(
      MEMORIA_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_SUCCESS_ELIMINAR,
          { entity: value, ...MSG_PARAMS.GENDER.MALE }
        );
      })
    ).subscribe((value) => this.textoDeleteSuccess = value);

    this.translate.get(
      MEMORIA_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_ERROR_ELIMINAR,
          { entity: value, ...MSG_PARAMS.GENDER.MALE }
        );
      })
    ).subscribe((value) => this.textoDeleteError = value);

    this.translate.get(
      PETICION_EVALUACION_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_BUTTON_SAVE,
          { entity: value, ...MSG_PARAMS.GENDER.MALE }
        );
      })
    ).subscribe((value) => this.textoCrear = value);
  }

  protected createObservable(reset?: boolean): Observable<SgiRestListResult<IMemoriaPeticionEvaluacion>> {
    return this.memoriaService.findAllMemoriasEvaluacionByPersonaRef(this.getFindOptions(reset));
  }

  protected initColumns(): void {
    this.displayedColumns = ['numReferencia', 'comite', 'estadoActual', 'fechaEvaluacion', 'fechaLimite', 'acciones'];
  }

  protected createFilter(): SgiRestFilter {
    const controls = this.formGroup.controls;
    return new RSQLSgiRestFilter('comite.id', SgiRestFilterOperator.EQUALS, controls.comite.value?.id?.toString())
      .and('peticionEvaluacion.titulo', SgiRestFilterOperator.LIKE_ICASE, controls.titulo.value)
      .and('numReferencia', SgiRestFilterOperator.LIKE_ICASE, controls.numReferencia.value)
      .and('estadoActual.id', SgiRestFilterOperator.EQUALS, controls.tipoEstadoMemoria.value?.toString());
  }

  protected loadTable(reset?: boolean) {
    // TODO Do the request with paginator/sort/filter values
    this.memorias$ = this.getObservableLoadTable(reset);
  }

  hasPermisoEnviarSecretaria(estadoMemoriaId: number, solicitanteRef: string): boolean {
    // Si el estado es 'Completada', 'Favorable pendiente de modificaciones mínima',
    // 'Pendiente de correcciones', 'Completada seguimiento anual',
    // 'Completada seguimiento final' o 'En aclaracion seguimiento final' se muestra el botón de enviar.
    if ((estadoMemoriaId === ESTADO_MEMORIA.COMPLETADA || estadoMemoriaId === ESTADO_MEMORIA.FAVORABLE_PENDIENTE_MODIFICACIONES_MINIMAS
      || estadoMemoriaId === ESTADO_MEMORIA.PENDIENTE_CORRECCIONES || estadoMemoriaId === ESTADO_MEMORIA.COMPLETADA_SEGUIMIENTO_ANUAL
      || estadoMemoriaId === ESTADO_MEMORIA.COMPLETADA_SEGUIMIENTO_FINAL
      || estadoMemoriaId === ESTADO_MEMORIA.EN_ACLARACION_SEGUIMIENTO_FINAL) && this.isUserSolicitantePeticionEvaluacion(solicitanteRef)) {
      return true;
    } else {
      return false;
    }
  }

  hasPermisoEliminar(estadoMemoriaId: number, solicitanteRef: string): boolean {
    // Si el estado es 'En elaboración' o 'Completada'.
    return (estadoMemoriaId === 1 || estadoMemoriaId === 2) && this.isUserSolicitantePeticionEvaluacion(solicitanteRef);
  }

  enviarSecretaria(memoria: IMemoriaPeticionEvaluacion) {
    this.suscripciones.push(this.memoriaService.checkDatosAdjuntosEnviarSecretariaExists(memoria.id).pipe(
      map(respuesta => {
        if (respuesta) {
          this.suscripciones.push(this.dialogService.showConfirmation(MSG_CONFIRM_ENVIAR_SECRETARIA)
            .pipe(switchMap((aceptado) => {
              if (aceptado) {
                return this.memoriaService.enviarSecretaria(memoria.id);
              }
            })).subscribe(
              () => {
                this.loadTable();
                this.snackBarService.showSuccess(MSG_SUCCESS_ENVIAR_SECRETARIA);
              },
              (error) => {
                this.logger.error(error);
                if (error instanceof SgiError) {
                  this.snackBarService.showError(error);
                }
                else {
                  this.snackBarService.showError(MSG_ERROR_ENVIAR_SECRETARIA);
                }
              }
            ));
        } else {
          this.suscripciones.push(this.dialogService.showConfirmation(MSG_ERROR_DATOS_ADJUNTOS)
            .pipe(switchMap((consentimiento) => {
              if (consentimiento) {
                return this.memoriaService.enviarSecretaria(memoria.id);
              }
            })).subscribe(
              () => {
                this.loadTable();
                this.snackBarService.showSuccess(MSG_SUCCESS_ENVIAR_SECRETARIA);
              },
              (error) => {
                this.logger.error(error);
                if (error instanceof SgiError) {
                  this.snackBarService.showError(error);
                }
                else {
                  this.snackBarService.showError(MSG_ERROR_ENVIAR_SECRETARIA);
                }
              }
            ));
          return of();
        }
      })).subscribe());
  }

  /**
   * Elimina la memoria.
   *
   * @param idMemoria Identificador de la memoria
   */
  delete(idMemoria: number): void {
    this.dialogService.showConfirmation(this.textoDelete)
      .pipe(switchMap((aceptado) => {
        if (aceptado) {
          return this.memoriaService.deleteById(idMemoria);
        }
        return of();
      })).subscribe(
        () => {
          this.loadTable();
          this.snackBarService.showSuccess(this.textoDeleteSuccess);
        },
        (error) => {
          this.logger.error(error);
          if (error instanceof SgiError) {
            this.snackBarService.showError(error);
          }
          else {
            this.snackBarService.showError(this.textoDeleteError);
          }
        }
      );
  }

  hasPermisoEnviarSecretariaRetrospectiva(memoria: IMemoria, solicitanteRef: string): boolean {
    // Si el estado es 'Completada', es de tipo CEEA y requiere retrospectiva se muestra el botón de enviar.
    // Si la retrospectiva ya está 'En secretaría' no se muestra el botón.
    // El estado de la memoria debe de ser mayor a FIN_EVALUACION
    return (memoria.estadoActual.id >= ESTADO_MEMORIA.FIN_EVALUACION && memoria.comite.id === COMITE.CEEA && memoria.requiereRetrospectiva
      && memoria.retrospectiva.estadoRetrospectiva.id === ESTADO_RETROSPECTIVA.COMPLETADA
      && this.isUserSolicitantePeticionEvaluacion(solicitanteRef));
  }

  enviarSecretariaRetrospectiva(memoria: IMemoriaPeticionEvaluacion) {
    this.dialogService.showConfirmation(MSG_CONFIRM_ENVIAR_SECRETARIA_RETROSPECTIVA)
      .pipe(switchMap((aceptado) => {
        if (aceptado) {
          return this.memoriaService.enviarSecretariaRetrospectiva(memoria.id);
        }
        return of();
      })).subscribe(
        () => {
          this.loadTable();
          this.snackBarService.showSuccess(MSG_SUCCESS_ENVIAR_SECRETARIA_RETROSPECTIVA);
        },
        (error) => {
          this.logger.error(error);
          if (error instanceof SgiError) {
            this.snackBarService.showError(error);
          }
          else {
            this.snackBarService.showError(MSG_ERROR_ENVIAR_SECRETARIA_RETROSPECTIVA);
          }
        }
      );
  }

  private isUserSolicitantePeticionEvaluacion(userRef: string): boolean {
    return userRef === this.authService.authStatus$.value.userRefId;
  }

  public openExportModal() {
    const data: IMemoriaListadoModalData = {
      findOptions: this.findOptions,
      isInvestigador: true,
      totalRegistrosExportacionExcel: this.totalElementos,
      limiteRegistrosExportacionExcel: Number(this.limiteRegistrosExportacionExcel)
    };

    const config = {
      data
    };
    this.matDialog.open(MemoriaListadoExportModalComponent, config);
  }

  isMemoriaSeguimiento(estadoMemoriaId: number): boolean {
    if (estadoMemoriaId === ESTADO_MEMORIA.COMPLETADA_SEGUIMIENTO_ANUAL
      || estadoMemoriaId === ESTADO_MEMORIA.COMPLETADA_SEGUIMIENTO_FINAL
      || estadoMemoriaId === ESTADO_MEMORIA.EN_ACLARACION_SEGUIMIENTO_FINAL) {
      return true;
    } else {
      return false;
    }
  }

}
