import { Component, OnInit } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { MatDialog, MatDialogConfig } from '@angular/material/dialog';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { AbstractTablePaginationComponent } from '@core/component/abstract-table-pagination.component';
import { SgiError } from '@core/errors/sgi-error';
import { MSG_PARAMS } from '@core/i18n';
import { INotificacionProyectoExternoCVN } from '@core/models/csp/notificacion-proyecto-externo-cvn';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { NotificacionProyectoExternoCvnService } from '@core/services/csp/notificacion-proyecto-externo-cvn/notificacion-proyecto-externo-cvn.service';
import { DialogService } from '@core/services/dialog.service';
import { EmpresaService } from '@core/services/sgemp/empresa.service';
import { PersonaService } from '@core/services/sgp/persona.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { TranslateService } from '@ngx-translate/core';
import { RSQLSgiRestFilter, SgiRestFilter, SgiRestFilterOperator, SgiRestListResult } from '@sgi/framework/http';
import { NGXLogger } from 'ngx-logger';
import { EMPTY, from, Observable, of } from 'rxjs';
import { catchError, concatMap, filter, map, mergeMap, switchMap, toArray } from 'rxjs/operators';
import { TipoColectivo } from 'src/app/esb/sgp/shared/select-persona/select-persona.component';
import { CSP_ROUTE_NAMES } from '../../csp-route-names';
import { NotificacionCvnAsociarAutorizacionModalComponent } from '../modals/notificacion-cvn-asociar-autorizacion-modal/notificacion-cvn-asociar-autorizacion-modal.component';
import { NotificacionCvnAsociarProyectoModalComponent } from '../modals/notificacion-cvn-asociar-proyecto-modal/notificacion-cvn-asociar-proyecto-modal.component';

const MSG_ERROR_LOAD = marker('error.load');
const MSG_ASOCIAR_SUCCESS = marker('msg.asociar.entity.success');
const AUTORIZACION_KEY = marker('csp.autorizacion');
const PROYECTO_KEY = marker('csp.proyecto');
const MGS_DESASOCIAR_AUTORIZACION_KEY = marker('csp.notificacion-cvn.desasociar-autorizacion');
const MGS_DESASOCIAR_PROYECTO_KEY = marker('csp.notificacion-cvn.desasociar-proyecto');
const MSG_ERROR_DESASOCIAR = marker('error.desasociar.entity');
const MSG_SUCCESS_DESASOCIAR = marker('msg.desasociar.entity.success');

@Component({
  selector: 'sgi-notificacion-cvn-listado',
  templateUrl: './notificacion-cvn-listado.component.html',
  styleUrls: ['./notificacion-cvn-listado.component.scss']
})
export class NotificacionCvnListadoComponent extends AbstractTablePaginationComponent<INotificacionProyectoExternoCVN> implements OnInit {
  fxLayoutProperties: FxLayoutProperties;

  notificaciones$: Observable<INotificacionProyectoExternoCVN[]>;

  textoAsociarAutorizacionSuccess: string;
  textoAsociarProyectoSuccess: string;
  textoSuccessDesasociarAutorizacion: string;
  textoSuccessDesasociarProyecto: string;
  textoDesasociarAutorizacion: string;
  textoDesasociarProyecto: string;
  textoErrorDesasociarProyecto: string;
  textoErrorDesasociarAutorizacion: string;
  msgParamAutorizacionEntity = {};
  msgParamProyectoEntity = {};

  TIPO_COLECTIVO = TipoColectivo;

  CSP_ROUTE_NAMES = CSP_ROUTE_NAMES;

  constructor(
    protected readonly snackBarService: SnackBarService,
    protected readonly logger: NGXLogger,
    private readonly notificacionProyectoExternoCvnService: NotificacionProyectoExternoCvnService,
    private readonly personaService: PersonaService,
    private matDialog: MatDialog,
    private readonly empresaService: EmpresaService,
    private readonly translate: TranslateService,
    private dialogService: DialogService,
  ) {
    super(snackBarService, MSG_ERROR_LOAD);
    this.initFlexProperties();
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.setupI18N();
    this.initFormGroup();
  }

  private setupI18N(): void {
    this.translate.get(
      AUTORIZACION_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_ASOCIAR_SUCCESS,
          { entity: value, ...MSG_PARAMS.GENDER.FEMALE }
        );
      })
    ).subscribe((value) => this.textoAsociarAutorizacionSuccess = value);

    this.translate.get(
      PROYECTO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_ASOCIAR_SUCCESS,
          { entity: value, ...MSG_PARAMS.GENDER.MALE }
        );
      })
    ).subscribe((value) => this.textoAsociarProyectoSuccess = value);

    this.translate.get(
      AUTORIZACION_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamAutorizacionEntity = { entity: value });

    this.translate.get(
      PROYECTO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamProyectoEntity = { entity: value });

    this.translate.get(
      AUTORIZACION_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_SUCCESS_DESASOCIAR,
          { entity: value, ...MSG_PARAMS.GENDER.FEMALE }
        );
      })
    ).subscribe((value) => this.textoSuccessDesasociarAutorizacion = value);

    this.translate.get(
      AUTORIZACION_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_ERROR_DESASOCIAR,
          { entity: value, ...MSG_PARAMS.GENDER.FEMALE }
        );
      })
    ).subscribe((value) => this.textoErrorDesasociarAutorizacion = value);

    this.translate.get(
      MGS_DESASOCIAR_AUTORIZACION_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.textoDesasociarAutorizacion = value);

    this.translate.get(
      PROYECTO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_SUCCESS_DESASOCIAR,
          { entity: value, ...MSG_PARAMS.GENDER.MALE }
        );
      })
    ).subscribe((value) => this.textoSuccessDesasociarProyecto = value);

    this.translate.get(
      PROYECTO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_ERROR_DESASOCIAR,
          { entity: value, ...MSG_PARAMS.GENDER.MALE }
        );
      })
    ).subscribe((value) => this.textoErrorDesasociarProyecto = value);

    this.translate.get(
      MGS_DESASOCIAR_PROYECTO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.textoDesasociarProyecto = value);

  }

  private initFormGroup(): void {
    this.formGroup = new FormGroup({
      investigador: new FormControl(null),
      titulo: new FormControl(''),
      fechaInicioProyectoDesde: new FormControl(null),
      fechaInicioProyectoHasta: new FormControl(null),
      fechaFinProyectoDesde: new FormControl(null),
      fechaFinProyectoHasta: new FormControl(null),
      entidadParticipacion: new FormControl(null)
    });
  }

  protected createObservable(reset?: boolean): Observable<SgiRestListResult<INotificacionProyectoExternoCVN>> {
    return this.notificacionProyectoExternoCvnService.findAll(this.getFindOptions(reset)).pipe(
      switchMap(notificaciones => from(notificaciones.items).pipe(
        concatMap(notificacion => this.fillNotificacionSolicitante(notificacion)),
        concatMap(notificacion => this.fillNotificacionEntidadParticipacion(notificacion)),
        concatMap(notificacion => this.fillNotificacionInvestigadorPrincipal(notificacion)),
        toArray(),
        map(items => {
          notificaciones.items = items;
          return notificaciones;
        })
      ))
    );
  }

  private fillNotificacionSolicitante(notificacion: INotificacionProyectoExternoCVN): Observable<INotificacionProyectoExternoCVN> {
    if (!notificacion.solicitante?.id) {
      return of(notificacion);
    }
    return this.personaService.findById(notificacion.solicitante.id).pipe(
      map(solicitante => {
        notificacion.solicitante = solicitante;
        return notificacion;
      }),
      catchError((error) => {
        this.logger.error(error);
        return of(notificacion);
      })
    );
  }

  private fillNotificacionEntidadParticipacion(notificacion: INotificacionProyectoExternoCVN): Observable<INotificacionProyectoExternoCVN> {
    if (!notificacion.entidadParticipacion?.id) {
      return of(notificacion);
    }
    return this.empresaService.findById(notificacion.entidadParticipacion.id).pipe(
      map(entidadParticipacion => {
        notificacion.entidadParticipacion = entidadParticipacion;
        return notificacion;
      }),
      catchError((error) => {
        this.logger.error(error);
        return of(notificacion);
      })

    );
  }

  private fillNotificacionInvestigadorPrincipal(
    notificacion: INotificacionProyectoExternoCVN
  ): Observable<INotificacionProyectoExternoCVN> {
    if (!notificacion.responsable?.id) {
      return of(notificacion);
    }
    return this.personaService.findById(notificacion.responsable.id).pipe(
      map(responsable => {
        notificacion.responsable = responsable;
        return notificacion;
      }),
      catchError((error) => {
        this.logger.error(error);
        return of(notificacion);
      })
    );
  }


  protected initColumns(): void {
    this.columnas = ['investigador', 'titulo', 'entidadParticipacion', 'ip', 'fechaInicio', 'fechaFin', 'acciones'];
  }

  protected loadTable(reset?: boolean): void {
    this.notificaciones$ = this.getObservableLoadTable(reset);
  }

  protected createFilter(): SgiRestFilter {
    const controls = this.formGroup.controls;
    return new RSQLSgiRestFilter(
      'solicitanteRef', SgiRestFilterOperator.EQUALS, controls.investigador.value?.id)
      .and('titulo', SgiRestFilterOperator.LIKE_ICASE, controls.titulo.value)
      .and('fechaInicio', SgiRestFilterOperator.GREATHER_OR_EQUAL, LuxonUtils.toBackend(controls.fechaInicioProyectoDesde.value))
      .and('fechaInicio', SgiRestFilterOperator.LOWER_OR_EQUAL, LuxonUtils.toBackend(controls.fechaInicioProyectoHasta.value))
      .and('fechaFin', SgiRestFilterOperator.GREATHER_OR_EQUAL, LuxonUtils.toBackend(controls.fechaFinProyectoDesde.value))
      .and('fechaFin', SgiRestFilterOperator.LOWER_OR_EQUAL, LuxonUtils.toBackend(controls.fechaFinProyectoHasta.value))
      .and('entidadParticipacionRef', SgiRestFilterOperator.EQUALS, controls.entidadParticipacion.value?.id);
  }

  onClearFilters() {
    super.onClearFilters();
    this.formGroup.controls.fechaInicioProyectoDesde.setValue(null);
    this.formGroup.controls.fechaInicioProyectoHasta.setValue(null);
    this.formGroup.controls.fechaFinProyectoDesde.setValue(null);
    this.formGroup.controls.fechaFinProyectoHasta.setValue(null);
    this.onSearch();
  }

  private initFlexProperties(): void {
    this.fxLayoutProperties = new FxLayoutProperties();
    this.fxLayoutProperties.gap = '1%';
    this.fxLayoutProperties.layout = 'row wrap';
    this.fxLayoutProperties.xs = 'column';
  }


  openModalAsociarAutorizacion(data: INotificacionProyectoExternoCVN): void {
    const config: MatDialogConfig<INotificacionProyectoExternoCVN> = {
      panelClass: 'sgi-dialog-container',
      data
    };
    const dialogRef = this.matDialog.open(NotificacionCvnAsociarAutorizacionModalComponent, config);
    dialogRef.afterClosed().pipe(
      filter(result => !!result)
    ).subscribe(() => {
      this.snackBarService.showSuccess(this.textoAsociarAutorizacionSuccess);
    });
  }

  openModalAsociarProyecto(data: INotificacionProyectoExternoCVN): void {
    const config: MatDialogConfig<INotificacionProyectoExternoCVN> = {
      panelClass: 'sgi-dialog-container',
      data
    };
    const dialogRef = this.matDialog.open(NotificacionCvnAsociarProyectoModalComponent, config);
    dialogRef.afterClosed().pipe(
      filter(result => !!result)
    ).subscribe(() => {
      this.snackBarService.showSuccess(this.textoAsociarProyectoSuccess);
    });
  }

  desasociarAutorizacion(data: INotificacionProyectoExternoCVN) {
    const subcription = this.dialogService.showConfirmation(this.textoDesasociarAutorizacion).pipe(
      switchMap((accept) => {
        if (accept) {
          return this.notificacionProyectoExternoCvnService.asociarAutorizacion(data.id, {} as INotificacionProyectoExternoCVN);
        }
      })).subscribe(
        () => {
          this.snackBarService.showSuccess(this.textoSuccessDesasociarAutorizacion);
          this.loadTable();
        },
        (error) => {
          this.logger.error(error);
          if (error instanceof SgiError) {
            this.snackBarService.showError(error);
          }
          else {
            this.snackBarService.showError(this.textoErrorDesasociarProyecto);
          }
        }
      );
    this.suscripciones.push(subcription);
  }

  desasociarProyecto(data: INotificacionProyectoExternoCVN) {
    const subcription = this.dialogService.showConfirmation(this.textoDesasociarProyecto).pipe(
      switchMap((accept) => {
        if (accept) {
          return this.notificacionProyectoExternoCvnService.asociarProyecto(data.id, {} as INotificacionProyectoExternoCVN);
        }
      })).subscribe(
        () => {
          this.snackBarService.showSuccess(this.textoSuccessDesasociarProyecto);
          this.loadTable();
        },
        (error) => {
          this.logger.error(error);
          if (error instanceof SgiError) {
            this.snackBarService.showError(error);
          }
          else {
            this.snackBarService.showError(this.textoErrorDesasociarAutorizacion);
          }
        }
      );
    this.suscripciones.push(subcription);
  }
}
