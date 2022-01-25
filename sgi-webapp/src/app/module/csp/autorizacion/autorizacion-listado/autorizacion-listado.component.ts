import { Component, OnInit } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { AbstractTablePaginationComponent } from '@core/component/abstract-table-pagination.component';
import { HttpProblem } from '@core/errors/http-problem';
import { MSG_PARAMS } from '@core/i18n';
import { IAutorizacion } from '@core/models/csp/autorizacion';
import { Estado, ESTADO_MAP, IEstadoAutorizacion } from '@core/models/csp/estado-autorizacion';
import { FxFlexProperties } from '@core/models/shared/flexLayout/fx-flex-properties';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { ROUTE_NAMES } from '@core/route.names';
import { AutorizacionService } from '@core/services/csp/autorizacion/autorizacion.service';
import { EstadoAutorizacionService } from '@core/services/csp/estado-autorizacion/estado-autorizacion.service';
import { DialogService } from '@core/services/dialog.service';
import { EmpresaService } from '@core/services/sgemp/empresa.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { TranslateService } from '@ngx-translate/core';
import { SgiAuthService } from '@sgi/framework/auth';
import { RSQLSgiRestFilter, SgiRestFilter, SgiRestFilterOperator, SgiRestListResult } from '@sgi/framework/http';
import { DateTime } from 'luxon';
import { NGXLogger } from 'ngx-logger';
import { from, Observable, of } from 'rxjs';
import { map, mergeMap, switchMap, tap, toArray } from 'rxjs/operators';

const MSG_BUTTON_ADD = marker('btn.add.entity');
const MSG_ERROR_LOAD = marker('error.load');
const MSG_DELETE = marker('msg.delete.entity');
const MSG_ERROR_DELETE = marker('error.delete.entity');
const MSG_SUCCESS_DELETE = marker('msg.delete.entity.success');
const AUTORIZACION_KEY = marker('csp.autorizacion');

export interface IAutorizacionListado {
  autorizacion: IAutorizacion;
  estadoAutorizacion: IEstadoAutorizacion;
  fechaEstadoBorrador: DateTime;
  entidadPaticipacionNombre: string;
  hasCertificadoVisible: boolean;
}

@Component({
  selector: 'sgi-autorizacion-listado',
  templateUrl: './autorizacion-listado.component.html',
  styleUrls: ['./autorizacion-listado.component.scss']
})
export class AutorizacionListadoComponent extends AbstractTablePaginationComponent<IAutorizacionListado> implements OnInit {
  ROUTE_NAMES = ROUTE_NAMES;

  fxFlexProperties: FxFlexProperties;
  fxLayoutProperties: FxLayoutProperties;
  autorizaciones$: Observable<IAutorizacionListado[]>;
  textoCrear: string;
  textoDelete: string;
  textoSuccessDelete: string;
  textoErrorDelete: string;
  mapCanBeDeleted: Map<number, boolean> = new Map();
  isInvestigador: boolean;

  get ESTADO_MAP() {
    return ESTADO_MAP;
  }

  constructor(
    private readonly logger: NGXLogger,
    protected snackBarService: SnackBarService,
    private autorizacionService: AutorizacionService,
    private estadoAutorizacionService: EstadoAutorizacionService,
    private empresaService: EmpresaService,
    private dialogService: DialogService,
    public authService: SgiAuthService,
    private readonly translate: TranslateService,
  ) {
    super(snackBarService, MSG_ERROR_LOAD);
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
    });
    this.filter = this.createFilter();

    this.isInvestigador = this.authService.hasAnyAuthority(['CSP-AUT-INV-C', 'CSP-AUT-INV-ER', 'CSP-AUT-INV-BR']);

  }

  private setupI18N(): void {

    this.translate.get(
      AUTORIZACION_KEY,
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
    const autorizaciones$ = this.isInvestigador ? this.autorizacionService.findAllInvestigador(this.getFindOptions(reset)) : this.autorizacionService.findAll(this.getFindOptions(reset));
    return autorizaciones$.pipe(
      map(result => {
        const autorizaciones = result.items.map((autorizacion) => {
          return {
            autorizacion,
            estadoAutorizacion: {} as IEstadoAutorizacion,
            fechaEstadoBorrador: null,
            entidadPaticipacionNombre: null,
            hasCertificadoVisible: null,
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
                  if (estadoAutorizacion.estado === Estado.BORRADOR) {
                    autorizacionListado.fechaEstadoBorrador = estadoAutorizacion.fecha;
                  }
                  autorizacionListado.estadoAutorizacion = estadoAutorizacion;
                  return autorizacionListado;
                })
              );
            }
            return of(autorizacionListado);
          }),
          mergeMap(autorizacionListado => {
            if (autorizacionListado.autorizacion.id) {
              return this.autorizacionService.hasCertificadoAutorizacionVisible(autorizacionListado.autorizacion.id).pipe(
                map(exist => {
                  autorizacionListado.hasCertificadoVisible = exist;
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
                }));
            } else {
              autorizacionListado.entidadPaticipacionNombre = autorizacionListado?.autorizacion?.datosEntidad;
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
      'fechaSolicitud', 'tituloProyecto', 'entidadParticipacion', 'estado',
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
      .and('estado.estado', SgiRestFilterOperator.EQUALS, controls.estado.value);
  }

  onClearFilters() {
    super.onClearFilters();
    this.formGroup.controls.fechaSolicitudInicio.setValue(null);
    this.formGroup.controls.fechaSolicitudFin.setValue(null);
    this.formGroup.controls.estado.setValue(null);
    this.onSearch();
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
          if (error instanceof HttpProblem) {
            this.snackBarService.showError(error);
          }
          else {
            this.snackBarService.showError(this.textoErrorDelete);
          }
        }
      );
    this.suscripciones.push(subcription);
  }

}
