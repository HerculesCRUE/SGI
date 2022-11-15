import { Component, OnInit } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { AbstractTablePaginationComponent } from '@core/component/abstract-table-pagination.component';
import { SgiError } from '@core/errors/sgi-error';
import { MSG_PARAMS } from '@core/i18n';
import { IAutorizacionWithFirstEstado } from '@core/models/csp/autorizacion-with-first-estado';
import { ICertificadoAutorizacion } from '@core/models/csp/certificado-autorizacion';
import { ESTADO_MAP, IEstadoAutorizacion } from '@core/models/csp/estado-autorizacion';
import { FxFlexProperties } from '@core/models/shared/flexLayout/fx-flex-properties';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { ROUTE_NAMES } from '@core/route.names';
import { AutorizacionService } from '@core/services/csp/autorizacion/autorizacion.service';
import { EstadoAutorizacionService } from '@core/services/csp/estado-autorizacion/estado-autorizacion.service';
import { DialogService } from '@core/services/dialog.service';
import { DocumentoService } from '@core/services/sgdoc/documento.service';
import { EmpresaService } from '@core/services/sgemp/empresa.service';
import { PersonaService } from '@core/services/sgp/persona.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { TranslateService } from '@ngx-translate/core';
import { SgiAuthService } from '@sgi/framework/auth';
import { RSQLSgiRestFilter, SgiRestFilter, SgiRestFilterOperator, SgiRestListResult } from '@sgi/framework/http';
import { DateTime } from 'luxon';
import { NGXLogger } from 'ngx-logger';
import { EMPTY, from, Observable, of, Subscription } from 'rxjs';
import { catchError, map, mergeMap, switchMap, tap, toArray } from 'rxjs/operators';
import { TipoColectivo } from 'src/app/esb/sgp/shared/select-persona/select-persona.component';
import { CSP_ROUTE_NAMES } from '../../csp-route-names';

const MSG_BUTTON_ADD = marker('btn.add.entity');
const MSG_DELETE = marker('msg.delete.entity');
const MSG_ERROR_DELETE = marker('error.delete.entity');
const MSG_SUCCESS_DELETE = marker('msg.delete.entity.success');
const AUTORIZACION_KEY = marker('csp.autorizacion');
const AUTORIZACION_SOLICITUD_KEY = marker('csp.autorizacion-solicitud');
const NOTIFICACION_KEY = marker('csp.notificacion-cvn');
const PROYECTO_KEY = marker('csp.proyecto');

export interface IAutorizacionListado {
  autorizacion: IAutorizacionWithFirstEstado;
  estadoAutorizacion: IEstadoAutorizacion;
  fechaEstado: DateTime;
  entidadPaticipacionNombre: string;
  certificadoVisible: ICertificadoAutorizacion;
  proyectoId: number;
  notificacionId: number;
}

@Component({
  selector: 'sgi-autorizacion-listado',
  templateUrl: './autorizacion-listado.component.html',
  styleUrls: ['./autorizacion-listado.component.scss']
})
export class AutorizacionListadoComponent extends AbstractTablePaginationComponent<IAutorizacionListado> implements OnInit {
  ROUTE_NAMES = ROUTE_NAMES;
  CSP_ROUTE_NAMES = CSP_ROUTE_NAMES;

  private subscriptions: Subscription[] = [];
  fxFlexProperties: FxFlexProperties;
  fxLayoutProperties: FxLayoutProperties;
  autorizaciones$: Observable<IAutorizacionListado[]>;
  textoCrear: string;
  textoDelete: string;
  textoSuccessDelete: string;
  textoErrorDelete: string;
  mapCanBeDeleted: Map<number, boolean> = new Map();

  msgParamNotificacionEntity = {};
  msgParamProyectoEntity = {};

  get TIPO_COLECTIVO() {
    return TipoColectivo;
  }

  get ESTADO_MAP() {
    return ESTADO_MAP;
  }

  constructor(
    private readonly logger: NGXLogger,
    protected snackBarService: SnackBarService,
    private autorizacionService: AutorizacionService,
    private estadoAutorizacionService: EstadoAutorizacionService,
    private empresaService: EmpresaService,
    private personaService: PersonaService,
    private dialogService: DialogService,
    private documentoService: DocumentoService,
    public authService: SgiAuthService,
    private readonly translate: TranslateService,
  ) {
    super();
    this.fxFlexProperties = new FxFlexProperties();
    this.fxFlexProperties.sm = '0 1 calc(50%-10px)';
    this.fxFlexProperties.md = '0 1 calc(33%-10px)';
    this.fxFlexProperties.gtMd = '0 1 calc(18%-10px)';
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
      fechaSolicitudInicio: new FormControl(null),
      fechaSolicitudFin: new FormControl(null),
      estado: new FormControl(null),
      solicitante: new FormControl(null),
    });
    this.filter = this.createFilter();
  }

  private setupI18N(): void {

    this.translate.get(
      PROYECTO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamProyectoEntity = { entity: value, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      NOTIFICACION_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamNotificacionEntity = { entity: value, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      AUTORIZACION_SOLICITUD_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_BUTTON_ADD,
          { entity: value }
        );
      })
    ).subscribe((value) => this.textoCrear = value);

    this.translate.get(
      AUTORIZACION_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_DELETE,
          { entity: value, ...MSG_PARAMS.GENDER.FEMALE }
        );
      })
    ).subscribe((value) => this.textoDelete = value);

    this.translate.get(
      AUTORIZACION_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_ERROR_DELETE,
          { entity: value, ...MSG_PARAMS.GENDER.FEMALE }
        );
      })
    ).subscribe((value) => this.textoErrorDelete = value);

    this.translate.get(
      AUTORIZACION_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_SUCCESS_DELETE,
          { entity: value, ...MSG_PARAMS.GENDER.FEMALE }
        );
      })
    ).subscribe((value) => this.textoSuccessDelete = value);

  }

  protected createObservable(reset?: boolean): Observable<SgiRestListResult<IAutorizacionListado>> {
    return this.autorizacionService.findAll(this.getFindOptions(reset)).pipe(
      map(result => {
        const autorizaciones = result.items.map((autorizacion) => {
          return {
            autorizacion,
            estadoAutorizacion: {} as IEstadoAutorizacion,
            fechaEstado: null,
            entidadPaticipacionNombre: null,
            certificadoVisible: null
          } as IAutorizacionListado;
        });
        return {
          page: result.page,
          total: result.total,
          items: autorizaciones
        } as SgiRestListResult<IAutorizacionListado>;
      }),
      switchMap(response =>
        from(response.items).pipe(
          mergeMap(autorizacionListado => {
            return this.autorizacionService.hasAutorizacionNotificacionProyectoExterno(autorizacionListado.autorizacion.id).pipe(
              tap((hasAutorizacionNotificacionProyectoExterno) =>
                this.mapCanBeDeleted.set(autorizacionListado.autorizacion.id, hasAutorizacionNotificacionProyectoExterno)),
              map(() => autorizacionListado)
            );
          }),
          mergeMap(autorizacionListado => {
            if (autorizacionListado.autorizacion.estado.id) {
              return this.estadoAutorizacionService.findById(autorizacionListado.autorizacion.estado.id).pipe(
                map(estadoAutorizacion => {
                  autorizacionListado.estadoAutorizacion = estadoAutorizacion;
                  autorizacionListado.fechaEstado = estadoAutorizacion.fecha;

                  return autorizacionListado;
                })
              );
            }
            return of(autorizacionListado);
          }),
          mergeMap(autorizacionListado => {
            if (autorizacionListado.autorizacion.id) {
              return this.autorizacionService.findCertificadoAutorizacionVisible(autorizacionListado.autorizacion.id).pipe(
                map(certificado => {
                  autorizacionListado.certificadoVisible = certificado;
                  return autorizacionListado;
                })
              );
            }
            return of(autorizacionListado);
          }),
          mergeMap(autorizacionListado => {
            if (autorizacionListado?.autorizacion?.entidad?.id) {
              return this.empresaService.findById(autorizacionListado?.autorizacion?.entidad?.id).pipe(
                map((empresa) => {
                  autorizacionListado.entidadPaticipacionNombre = empresa?.nombre;
                  return autorizacionListado;
                }),
                catchError((error) => {
                  this.logger.error(error);
                  this.processError(error);
                  return EMPTY;
                }));
            } else {
              autorizacionListado.entidadPaticipacionNombre = autorizacionListado?.autorizacion?.datosEntidad;
              return of(autorizacionListado);
            }
          }),
          mergeMap(autorizacionListado => {
            if (autorizacionListado?.autorizacion?.id) {
              return this.autorizacionService.findNotificacionProyectoExterno(autorizacionListado?.autorizacion?.id).pipe(
                map((notificacion) => {
                  if (notificacion) {
                    autorizacionListado.notificacionId = notificacion.id;
                    autorizacionListado.proyectoId = notificacion.proyecto?.id;
                  }
                  return autorizacionListado;
                }));
            } else {
              return of(autorizacionListado);
            }
          }),
          mergeMap(autorizacionListado => {
            if (autorizacionListado?.autorizacion?.solicitante?.id) {
              return this.personaService.findById(autorizacionListado?.autorizacion?.solicitante?.id).pipe(
                map((persona) => {
                  autorizacionListado.autorizacion.solicitante = persona;
                  return autorizacionListado;
                }),
                catchError((error) => {
                  this.logger.error(error);
                  this.processError(error);
                  return EMPTY;
                }));
            } else {
              return of(autorizacionListado);
            }
          }),
          toArray(),
          map(() => {
            return response;
          })
        )
      )
    );
  }

  protected initColumns(): void {
    this.columnas = [
      'fechaFirstEstado', 'solicitante', 'tituloProyecto', 'entidadParticipacion', 'estado',
      'fechaEstado', 'acciones'
    ];
  }

  protected loadTable(reset?: boolean): void {
    this.autorizaciones$ = this.getObservableLoadTable(reset);
  }

  protected createFilter(): SgiRestFilter {
    const controls = this.formGroup.controls;
    return new RSQLSgiRestFilter(
      'estado.fecha', SgiRestFilterOperator.GREATHER_OR_EQUAL, LuxonUtils.toBackend(controls.fechaSolicitudInicio.value))
      .and('estado.fecha', SgiRestFilterOperator.LOWER_OR_EQUAL, LuxonUtils.toBackend(controls.fechaSolicitudFin.value))
      .and('estado.estado', SgiRestFilterOperator.EQUALS, controls.estado.value)
      .and('solicitanteRef', SgiRestFilterOperator.EQUALS, controls.solicitante.value?.id);
  }

  protected resetFilters(): void {
    super.resetFilters();
    this.formGroup.controls.fechaSolicitudInicio.setValue(null);
    this.formGroup.controls.fechaSolicitudFin.setValue(null);
    this.formGroup.controls.estado.setValue(null);
    this.formGroup.controls.solicitante.setValue(null);
  }

  public deleteAutorizacion(autorizacionId: number): void {
    this.autorizacionService.deleteById(autorizacionId);

    const subcription = this.dialogService.showConfirmation(this.textoDelete).pipe(
      switchMap((accept) => {
        if (accept) {
          return this.autorizacionService.deleteById(autorizacionId);
        }
      })).subscribe(
        () => {
          this.snackBarService.showSuccess(this.textoSuccessDelete);
          this.loadTable();
        },
        (error) => {
          this.logger.error(error);
          if (error instanceof SgiError) {
            this.processError(error);
          }
          else {
            this.processError(new SgiError(this.textoErrorDelete));
          }
        }
      );
    this.suscripciones.push(subcription);
  }

}
