import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { FragmentComponent } from '@core/component/fragment.component';
import { SgiError } from '@core/errors/sgi-error';
import { MSG_PARAMS } from '@core/i18n';
import { COMITE } from '@core/models/eti/comite';
import { ESTADO_RETROSPECTIVA } from '@core/models/eti/estado-retrospectiva';
import { IMemoria } from '@core/models/eti/memoria';
import { ESTADO_MEMORIA } from '@core/models/eti/tipo-estado-memoria';
import { FxFlexProperties } from '@core/models/shared/flexLayout/fx-flex-properties';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { DialogService } from '@core/services/dialog.service';
import { MemoriaService } from '@core/services/eti/memoria.service';
import { PeticionEvaluacionService } from '@core/services/eti/peticion-evaluacion.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { TranslateService } from '@ngx-translate/core';
import { SgiAuthService } from '@sgi/framework/auth';
import { NGXLogger } from 'ngx-logger';
import { Subscription, of } from 'rxjs';
import { filter, map, switchMap } from 'rxjs/operators';
import { MEMORIAS_ROUTE } from '../../../memoria/memoria-route-names';
import { PeticionEvaluacionActionService } from '../../peticion-evaluacion.action.service';
import { IMemoriaPeticionEvaluacionWithLastEvaluacion, MemoriasListadoFragment } from './memorias-listado.fragment';

const MSG_CONFIRM_DELETE = marker('msg.delete.entity');
const MSG_ESTADO_ANTERIOR_OK = marker('msg.eti.memoria.estado-anterior.success');
const MSG_ESTADO_ANTERIOR_ERROR = marker('error.eti.memoria.estado-anterior');
const MSG_RECUPERAR_ESTADO = marker('msg.eti.memoria.estado-anterior');
const MSG_SUCCESS_ENVIAR_SECRETARIA = marker('msg.eti.memoria.enviar-secretaria.success');
const MSG_ERROR_ENVIAR_SECRETARIA = marker('error.eti.memoria.enviar-secretaria');
const MSG_CONFIRM_ENVIAR_SECRETARIA = marker('msg.eti.memoria.enviar-secretaria');
const MSG_SUCCESS_ENVIAR_SECRETARIA_RETROSPECTIVA = marker('msg.eti.memoria.enviar-secretaria.retrospectiva.success');
const MSG_ERROR_ENVIAR_SECRETARIA_RETROSPECTIVA = marker('error.eti.memoria.enviar-secretaria.retrospectiva');
const MSG_CONFIRM_ENVIAR_SECRETARIA_RETROSPECTIVA = marker('msg.eti.memoria.enviar-secretaria.retrospectiva');
const MEMORIA_KEY = marker('eti.memoria');
const MSG_ERROR_DATOS_ADJUNTOS = marker('error.eti.memoria.enviar-secretaria.documentos-adjuntos');
const MSG_SUCCESS_NOTIFICAR_REV_MINIMA = marker('msg.eti.memoria.notificar-revision-minima.success');
const MSG_ERROR_NOTIFICAR_REV_MINIMA = marker('msg.eti.memoria.notificar-revision-minima.error');
const MSG_CONFIRMACION_NOTIFICAR_REV_MINIMA = marker('msg.eti.memoria.notificar-rev-minima.confirmacion');

@Component({
  selector: 'sgi-memorias-listado',
  templateUrl: './memorias-listado.component.html',
  styleUrls: ['./memorias-listado.component.scss']
})
export class MemoriasListadoComponent extends FragmentComponent implements OnInit, OnDestroy {
  MEMORIAS_ROUTE = MEMORIAS_ROUTE;

  fxFlexProperties: FxFlexProperties;
  fxLayoutProperties: FxLayoutProperties;

  elementosPagina: number[] = [5, 10, 25, 100];
  displayedColumns: string[] = ['numReferencia', 'comite', 'estadoActual', 'fechaEvaluacion', 'fechaLimite', 'acciones'];
  disableEnviarSecretaria = true;

  datasource: MatTableDataSource<StatusWrapper<IMemoriaPeticionEvaluacionWithLastEvaluacion>>
    = new MatTableDataSource<StatusWrapper<IMemoriaPeticionEvaluacionWithLastEvaluacion>>();

  private subscriptions: Subscription[] = [];
  private listadoFragment: MemoriasListadoFragment;

  @ViewChild(MatPaginator, { static: true }) paginator: MatPaginator;
  @ViewChild(MatSort, { static: true }) sort: MatSort;

  textoDelete: string;
  textoNotificarRevMinimaSuccess: string;
  textoNotificarRevMinimaError: string;
  msgParamEntity = {};

  get MSG_PARAMS() {
    return MSG_PARAMS;
  }

  get isModuleInv(): boolean {
    return this.listadoFragment.isModuleInv;
  }

  constructor(
    private readonly logger: NGXLogger,
    protected readonly dialogService: DialogService,
    protected readonly memoriaService: MemoriaService,
    protected readonly peticionEvaluacionService: PeticionEvaluacionService,
    protected readonly snackBarService: SnackBarService,
    private actionService: PeticionEvaluacionActionService,
    private readonly translate: TranslateService,
    private authService: SgiAuthService
  ) {
    super(actionService.FRAGMENT.MEMORIAS, actionService);
    this.listadoFragment = this.fragment as MemoriasListadoFragment;
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.setupI18N();
    this.datasource.paginator = this.paginator;
    this.datasource.sort = this.sort;
    this.subscriptions.push(this.listadoFragment.memorias$.subscribe((memorias) => {
      this.datasource.data = memorias;
    }));

    this.subscriptions.push(this.actionService.status$.subscribe((status) => {
      this.disableEnviarSecretaria = status.changes;
    }
    ));

    this.datasource.sortingDataAccessor =
      (wrapper: StatusWrapper<IMemoriaPeticionEvaluacionWithLastEvaluacion>, property: string) => {
        switch (property) {
          case 'comite':
            return wrapper.value.comite?.comite;
          case 'estadoActual':
            return wrapper.value.estadoActual?.nombre;
          default:
            return wrapper.value[property];
        }
      };
  }

  private setupI18N(): void {
    this.translate.get(
      MEMORIA_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_CONFIRM_DELETE,
          { entity: value, ...MSG_PARAMS.GENDER.FEMALE }
        );
      })
    ).subscribe((value) => this.textoDelete = value);

    this.translate.get(
      MEMORIA_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamEntity = { entity: value, ...MSG_PARAMS.GENDER.FEMALE });

    this.translate.get(
      MSG_SUCCESS_NOTIFICAR_REV_MINIMA,
    ).subscribe((value) => this.textoNotificarRevMinimaSuccess = value);

    this.translate.get(
      MSG_ERROR_NOTIFICAR_REV_MINIMA
    ).subscribe((value) => this.textoNotificarRevMinimaError = value);
  }

  /**
   * Elimina la memoria.
   *
   * @param wrappedMemoria la memoria a eliminar.
   */
  delete(wrappedMemoria: StatusWrapper<IMemoriaPeticionEvaluacionWithLastEvaluacion>): void {
    this.subscriptions.push(this.dialogService.showConfirmation(
      this.textoDelete
    ).subscribe((aceptado) => {
      if (aceptado) {
        this.listadoFragment.deleteMemoria(wrappedMemoria);
      }
    }));
  }

  /**
   * Se recupera el estado anterior de la memoria
   * @param memoria la memoria a reestablecer el estado
   */
  private recuperarEstadoAnteriorMemoria(memoria: IMemoriaPeticionEvaluacionWithLastEvaluacion) {
    const recuperarEstadoMemoria = this.memoriaService.recuperarEstadoAnterior(memoria.id).pipe(
      map((response: IMemoria) => {
        if (response) {
          // Si todo ha salido bien se recarga la tabla con el cambio de estado actualizado y el aviso correspondiente
          this.snackBarService.showSuccess(MSG_ESTADO_ANTERIOR_OK);
          this.listadoFragment.loadMemorias(this.listadoFragment.getKey() as number);
        } else {
          // Si no se puede cambiar de estado se muestra el aviso
          this.snackBarService.showError(MSG_ESTADO_ANTERIOR_ERROR);
        }
      })
    ).subscribe();

    this.subscriptions.push(recuperarEstadoMemoria);
  }

  /**
   * Confirmación para recuperar el estado de la memoria
   * @param memoria la memoria a reestablecer el estado
   */
  public recuperarEstadoAnterior(memoria: IMemoriaPeticionEvaluacionWithLastEvaluacion) {
    const dialogServiceSubscription = this.dialogService.showConfirmation(MSG_RECUPERAR_ESTADO).pipe(
      map((aceptado: boolean) => {
        if (aceptado) {
          this.recuperarEstadoAnteriorMemoria(memoria);
        }
      })
    ).subscribe();

    this.subscriptions.push(dialogServiceSubscription);
  }

  hasPermisoEnviarSecretaria(memoria: IMemoriaPeticionEvaluacionWithLastEvaluacion): boolean {
    const estadosEnviarSecretaria = [
      ESTADO_MEMORIA.COMPLETADA,
      ESTADO_MEMORIA.FAVORABLE_PENDIENTE_MODIFICACIONES_MINIMAS,
      ESTADO_MEMORIA.PENDIENTE_CORRECCIONES,
      ESTADO_MEMORIA.COMPLETADA_SEGUIMIENTO_ANUAL,
      ESTADO_MEMORIA.COMPLETADA_SEGUIMIENTO_FINAL,
      ESTADO_MEMORIA.EN_ACLARACION_SEGUIMIENTO_FINAL,
      ESTADO_MEMORIA.SOLICITUD_MODIFICACION_SEGUIMIENTO_ANUAL
    ];

    return estadosEnviarSecretaria.includes(memoria.estadoActual.id);
  }

  enviarSecretaria(memoria: IMemoriaPeticionEvaluacionWithLastEvaluacion) {
    this.subscriptions.push(this.memoriaService.checkDatosAdjuntosEnviarSecretariaExists(memoria.id).pipe(
      map(respuesta => {
        if (respuesta) {
          this.subscriptions.push(this.dialogService.showConfirmation(MSG_CONFIRM_ENVIAR_SECRETARIA)
            .pipe(switchMap((aceptado) => {
              if (aceptado) {
                return this.memoriaService.enviarSecretaria(memoria.id);
              }
            })).subscribe(
              () => {
                this.listadoFragment.loadMemorias(this.listadoFragment.getKey() as number);
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
          this.subscriptions.push(this.dialogService.showConfirmation(MSG_ERROR_DATOS_ADJUNTOS)
            .pipe(switchMap((consentimiento) => {
              if (consentimiento) {
                return this.memoriaService.enviarSecretaria(memoria.id);
              }
            })).subscribe(
              () => {
                this.listadoFragment.loadMemorias(this.listadoFragment.getKey() as number);
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

  hasPermisoEnviarSecretariaRetrospectiva(memoria: IMemoria): boolean {
    // Si la retrospectiva ya está 'En secretaría' no se muestra el botón.
    // El estado de la memoria debe de ser mayor a FIN_EVALUACION
    return this.isUserSolicitantePeticionEvaluacion() && (memoria.estadoActual.id >= ESTADO_MEMORIA.FIN_EVALUACION && memoria.comite.id === COMITE.CEEA && memoria.requiereRetrospectiva
      && memoria.retrospectiva.estadoRetrospectiva.id === ESTADO_RETROSPECTIVA.COMPLETADA);
  }

  enviarSecretariaRetrospectiva(memoria: IMemoriaPeticionEvaluacionWithLastEvaluacion) {
    this.dialogService.showConfirmation(MSG_CONFIRM_ENVIAR_SECRETARIA_RETROSPECTIVA)
      .pipe(switchMap((aceptado) => {
        if (aceptado) {
          return this.memoriaService.enviarSecretariaRetrospectiva(memoria.id);
        }
        return of();
      })).subscribe(
        () => {
          this.listadoFragment.loadMemorias(this.listadoFragment.getKey() as number);
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

  hasPermisoEliminar(estadoMemoriaId: number): boolean {
    // Si el estado es 'En elaboración' o 'Completada'.
    return this.isUserSolicitantePeticionEvaluacion() && (estadoMemoriaId === 1 || estadoMemoriaId === 2);
  }

  ngOnDestroy(): void {
    this.subscriptions?.forEach(x => x.unsubscribe());
  }

  private isUserSolicitantePeticionEvaluacion(): boolean {
    return this.listadoFragment.solicitantePeticionEvaluacion?.id === this.authService.authStatus$.value.userRefId;
  }

  isMemoriaSeguimiento(memoria: IMemoriaPeticionEvaluacionWithLastEvaluacion): boolean {
    return [
      ESTADO_MEMORIA.COMPLETADA_SEGUIMIENTO_ANUAL,
      ESTADO_MEMORIA.COMPLETADA_SEGUIMIENTO_FINAL,
      ESTADO_MEMORIA.EN_ACLARACION_SEGUIMIENTO_FINAL,
      ESTADO_MEMORIA.SOLICITUD_MODIFICACION_SEGUIMIENTO_ANUAL
    ].includes(memoria.estadoActual.id);
  }

  public showRecuperarEstadoAnterior(memoria: IMemoriaPeticionEvaluacionWithLastEvaluacion): boolean {
    return [
      ESTADO_MEMORIA.EN_SECRETARIA,
      ESTADO_MEMORIA.EN_SECRETARIA_REVISION_MINIMA,
      ESTADO_MEMORIA.EN_EVALUACION,
      ESTADO_MEMORIA.ARCHIVADA,
      ESTADO_MEMORIA.EN_EVALUACION_REVISION_MINIMA
    ].includes(memoria.estadoActual.id);
  }

  public showNotificarRevisionMinima(memoria: IMemoriaPeticionEvaluacionWithLastEvaluacion): boolean {
    return ESTADO_MEMORIA.EN_SECRETARIA_REVISION_MINIMA === memoria.estadoActual.id;
  }

  public notificarRevisionMinima(memoria: IMemoriaPeticionEvaluacionWithLastEvaluacion): void {
    this.subscriptions.push(
      this.dialogService.showConfirmation(MSG_CONFIRMACION_NOTIFICAR_REV_MINIMA).pipe(
        filter(aceptado => !!aceptado),
        switchMap(() => this.memoriaService.notificarRevisionMinima(memoria.id))
      ).subscribe(() => {
        this.snackBarService.showSuccess(this.textoNotificarRevMinimaSuccess);
        this.listadoFragment.loadMemorias(this.listadoFragment.getKey() as number);
      },
        (error) => {
          if (error instanceof SgiError) {
            this.snackBarService.showError(error);
          } else {
            this.snackBarService.showError(new SgiError(this.textoNotificarRevMinimaError));
          }
        }
      )
    );
  }

}
