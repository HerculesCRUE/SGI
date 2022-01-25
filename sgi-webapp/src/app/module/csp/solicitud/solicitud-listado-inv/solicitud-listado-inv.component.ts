import { Component, OnInit } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { AbstractTablePaginationComponent } from '@core/component/abstract-table-pagination.component';
import { HttpProblem } from '@core/errors/http-problem';
import { MSG_PARAMS } from '@core/i18n';
import { IConvocatoria } from '@core/models/csp/convocatoria';
import { Estado, ESTADO_MAP } from '@core/models/csp/estado-solicitud';
import { ISolicitud } from '@core/models/csp/solicitud';
import { FxFlexProperties } from '@core/models/shared/flexLayout/fx-flex-properties';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { ROUTE_NAMES } from '@core/route.names';
import { SolicitudService } from '@core/services/csp/solicitud.service';
import { DialogService } from '@core/services/dialog.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { TranslateService } from '@ngx-translate/core';
import { SgiAuthService } from '@sgi/framework/auth';
import { RSQLSgiRestFilter, SgiRestFilter, SgiRestFilterOperator, SgiRestListResult } from '@sgi/framework/http';
import { NGXLogger } from 'ngx-logger';
import { merge, Observable, of } from 'rxjs';
import { map, switchMap, tap } from 'rxjs/operators';

const MSG_ERROR = marker('error.load');
const MSG_DEACTIVATE = marker('msg.deactivate.entity');
const MSG_SUCCESS_DEACTIVATE = marker('msg.csp.deactivate.success');
const MSG_ERROR_DEACTIVATE = marker('error.csp.deactivate.entity');
const SOLICITUD_KEY = marker('csp.solicitud');

interface SolicitudListado extends ISolicitud {
  convocatoria: IConvocatoria;
  modificable: boolean;
  eliminable: boolean;
}

@Component({
  selector: 'sgi-solicitud-listado',
  templateUrl: './solicitud-listado-inv.component.html',
  styleUrls: ['./solicitud-listado-inv.component.scss']
})
export class SolicitudListadoInvComponent extends AbstractTablePaginationComponent<SolicitudListado> implements OnInit {
  ROUTE_NAMES = ROUTE_NAMES;

  fxFlexProperties: FxFlexProperties;
  fxLayoutProperties: FxLayoutProperties;
  solicitudes$: Observable<SolicitudListado[]>;
  textoDesactivar: string;
  textoReactivar: string;
  textoErrorDesactivar: string;
  textoSuccessDesactivar: string;

  msgParamConvocatoriaExternaEntity = {};
  msgParamCodigoExternoEntity = {};
  msgParamObservacionesEntity = {};
  msgParamUnidadGestionEntity = {};

  get ESTADO_MAP() {
    return ESTADO_MAP;
  }

  get MSG_PARAMS() {
    return MSG_PARAMS;
  }

  constructor(
    private readonly logger: NGXLogger,
    private dialogService: DialogService,
    protected snackBarService: SnackBarService,
    private solicitudService: SolicitudService,
    private readonly translate: TranslateService,
    private sgiAuthService: SgiAuthService
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
      convocatoria: new FormControl(undefined),
      estadoSolicitud: new FormControl(''),
      tituloSolicitud: new FormControl(undefined),
    });
  }

  protected createObservable(reset?: boolean): Observable<SgiRestListResult<SolicitudListado>> {
    return this.solicitudService.findAllInvestigador(this.getFindOptions(reset)).pipe(
      map(response => {
        return response as SgiRestListResult<SolicitudListado>;
      }),
      switchMap(response => {
        const requestsConvocatoria: Observable<SolicitudListado>[] = [];
        response.items.forEach(solicitud => {
          if (solicitud.convocatoriaId) {
            requestsConvocatoria.push(this.solicitudService.findConvocatoria(solicitud.id).pipe(
              map(convocatoria => {
                solicitud.convocatoria = convocatoria;
                return solicitud;
              })
            ));
          }
          else {
            requestsConvocatoria.push(of(solicitud));
          }
        });
        return of(response).pipe(
          tap(() => merge(...requestsConvocatoria).subscribe())
        );
      }),
      switchMap((response) => {
        const requestsModificable: Observable<SolicitudListado>[] = [];
        response.items.forEach(solicitud => {
          requestsModificable.push(this.solicitudService.modificable(solicitud.id).pipe(
            map(isModificable => {
              solicitud.modificable = isModificable;
              return solicitud;
            })
          ));
        });
        return of(response).pipe(
          tap(() => merge(...requestsModificable).subscribe())
        );
      }),
      map((response) => {
        response.items.forEach(solicitud => {
          solicitud.eliminable = solicitud.estado.estado === Estado.BORRADOR
            && solicitud.creador.id === this.sgiAuthService.authStatus$?.getValue()?.userRefId;
        });
        return response;
      })
    );
  }

  protected initColumns(): void {
    this.columnas = [
      'codigoRegistroInterno',
      'codigoExterno',
      'referencia',
      'estado.estado',
      'titulo',
      'estado.fechaEstado',
      'acciones'
    ];
  }

  protected loadTable(reset?: boolean): void {
    this.solicitudes$ = this.getObservableLoadTable(reset);
  }

  protected createFilter(): SgiRestFilter {
    const controls = this.formGroup.controls;

    return new RSQLSgiRestFilter('convocatoria.id', SgiRestFilterOperator.EQUALS, controls.convocatoria.value?.id?.toString())
      .and('estado.estado', SgiRestFilterOperator.EQUALS, controls.estadoSolicitud.value)
      .and('titulo', SgiRestFilterOperator.LIKE_ICASE, controls.tituloSolicitud.value);
  }

  /**
   * Desactivar solicitud
   * @param solicitud una solicitud
   */
  deactivateSolicitud(solicitud: ISolicitud): void {
    const subcription = this.dialogService.showConfirmation(this.textoDesactivar).pipe(
      switchMap((accept) => {
        if (accept) {
          return this.solicitudService.desactivar(solicitud.id);
        } else {
          return of();
        }
      })
    ).subscribe(
      () => {
        this.snackBarService.showSuccess(this.textoSuccessDesactivar);
        this.loadTable();
      },
      (error) => {
        this.logger.error(error);
        if (error instanceof HttpProblem) {
          this.snackBarService.showError(error);
        }
        else {
          this.snackBarService.showError(this.textoErrorDesactivar);
        }
      }
    );
    this.suscripciones.push(subcription);
  }

  private setupI18N(): void {
    this.translate.get(
      SOLICITUD_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_DEACTIVATE,
          { entity: value }
        );
      })
    ).subscribe((value) => this.textoDesactivar = value);

    this.translate.get(
      SOLICITUD_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_ERROR_DEACTIVATE,
          { entity: value, ...MSG_PARAMS.GENDER.FEMALE }
        );
      })
    ).subscribe((value) => this.textoErrorDesactivar = value);

    this.translate.get(
      SOLICITUD_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_SUCCESS_DEACTIVATE,
          { entity: value, ...MSG_PARAMS.GENDER.FEMALE }
        );
      })
    ).subscribe((value) => this.textoSuccessDesactivar = value);
  }

}
