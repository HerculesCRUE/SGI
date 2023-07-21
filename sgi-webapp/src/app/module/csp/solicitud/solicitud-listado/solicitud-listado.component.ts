import { Component, OnInit } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { ActivatedRoute } from '@angular/router';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { AbstractTablePaginationComponent } from '@core/component/abstract-table-pagination.component';
import { IBaseExportModalData } from '@core/component/base-export/base-export-modal-data';
import { SgiError } from '@core/errors/sgi-error';
import { MSG_PARAMS } from '@core/i18n';
import { IConvocatoria } from '@core/models/csp/convocatoria';
import { Estado, ESTADO_MAP } from '@core/models/csp/estado-solicitud';
import { IPrograma } from '@core/models/csp/programa';
import { IProyecto } from '@core/models/csp/proyecto';
import { ISolicitanteExterno } from '@core/models/csp/solicitante-externo';
import { ISolicitud, TipoSolicitudGrupo } from '@core/models/csp/solicitud';
import { ISolicitudGrupo } from '@core/models/csp/solicitud-grupo';
import { FxFlexProperties } from '@core/models/shared/flexLayout/fx-flex-properties';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { ROUTE_NAMES } from '@core/route.names';
import { ConfigService } from '@core/services/cnf/config.service';
import { ConvocatoriaService } from '@core/services/csp/convocatoria.service';
import { ProgramaService } from '@core/services/csp/programa.service';
import { SolicitudService } from '@core/services/csp/solicitud.service';
import { DialogService } from '@core/services/dialog.service';
import { PersonaService } from '@core/services/sgp/persona.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { TranslateService } from '@ngx-translate/core';
import { SgiAuthService } from '@sgi/framework/auth';
import { RSQLSgiRestFilter, SgiRestFilter, SgiRestFilterOperator, SgiRestListResult } from '@sgi/framework/http';
import { NGXLogger } from 'ngx-logger';
import { BehaviorSubject, from, merge, Observable, of } from 'rxjs';
import { catchError, filter, map, mergeMap, switchMap, tap, toArray } from 'rxjs/operators';
import { TipoColectivo } from 'src/app/esb/sgp/shared/select-persona/select-persona.component';
import { CONVOCATORIA_ACTION_LINK_KEY } from '../../convocatoria/convocatoria.action.service';
import { ISolicitudCrearProyectoModalData, SolicitudCrearProyectoModalComponent } from '../modals/solicitud-crear-proyecto-modal/solicitud-crear-proyecto-modal.component';
import { SolicitudGrupoModalComponent } from '../modals/solicitud-grupo-modal/solicitud-grupo-modal.component';
import { SolicitudListadoExportModalComponent } from '../modals/solicitud-listado-export-modal/solicitud-listado-export-modal.component';

const MSG_BUTTON_NEW = marker('btn.add.entity');
const MSG_DEACTIVATE = marker('msg.deactivate.entity');
const MSG_SUCCESS_DEACTIVATE = marker('msg.csp.deactivate.success');
const MSG_ERROR_DEACTIVATE = marker('error.csp.deactivate.entity');
const MSG_REACTIVE = marker('msg.csp.reactivate');
const MSG_SUCCESS_REACTIVE = marker('msg.reactivate.entity.success');
const MSG_ERROR_REACTIVE = marker('error.reactivate.entity');
const MSG_SUCCESS_CREAR_PROYECTO = marker('msg.csp.solicitud.crear.proyecto');
const MSG_SUCCESS_CREAR_GRUPO = marker('msg.csp.solicitud.crear.grupo');
const SOLICITUD_KEY = marker('csp.solicitud');
const GRUPO_KEY = marker('csp.grupo');

export interface ISolicitudListadoData extends ISolicitud {
  convocatoria: IConvocatoria;
  showCreateGrupo: boolean;
  solicitanteExterno: ISolicitanteExterno;
  nombreSolicitante: string;
}

@Component({
  selector: 'sgi-solicitud-listado',
  templateUrl: './solicitud-listado.component.html',
  styleUrls: ['./solicitud-listado.component.scss']
})
export class SolicitudListadoComponent extends AbstractTablePaginationComponent<ISolicitudListadoData> implements OnInit {
  ROUTE_NAMES = ROUTE_NAMES;

  fxFlexProperties: FxFlexProperties;
  fxLayoutProperties: FxLayoutProperties;
  solicitudes$: Observable<ISolicitudListadoData[]>;
  textoCrear: string;
  textoDesactivar: string;
  textoReactivar: string;
  textoErrorDesactivar: string;
  textoSuccessDesactivar: string;
  textoSuccessReactivar: string;
  textoErrorReactivar: string;

  busquedaAvanzada = false;
  planInvestigaciones$: BehaviorSubject<IPrograma[]> = new BehaviorSubject<IPrograma[]>([]);

  mapCrearProyecto: Map<number, boolean> = new Map();
  mapModificable: Map<number, boolean> = new Map();

  msgParamConvocatoriaExternaEntity = {};
  msgParamCodigoExternoEntity = {};
  msgParamObservacionesEntity = {};
  msgParamUnidadGestionEntity = {};
  msgParamGrupoEntity = {};

  private convocatoriaId: number;

  private limiteRegistrosExportacionExcel: string;

  get tipoColectivoSolicitante() {
    return TipoColectivo.SOLICITANTE_CSP;
  }

  get tipoSolicitudGrupo() {
    return TipoSolicitudGrupo;
  }

  get ESTADO_MAP() {
    return ESTADO_MAP;
  }

  get MSG_PARAMS() {
    return MSG_PARAMS;
  }

  get Estado() {
    return Estado;
  }

  constructor(
    private readonly logger: NGXLogger,
    private dialogService: DialogService,
    protected snackBarService: SnackBarService,
    private solicitudService: SolicitudService,
    private personaService: PersonaService,
    private programaService: ProgramaService,
    private matDialog: MatDialog,
    private readonly translate: TranslateService,
    private convocatoriaService: ConvocatoriaService,
    private authService: SgiAuthService,
    route: ActivatedRoute,
    private readonly cnfService: ConfigService
  ) {
    super();
    this.fxFlexProperties = new FxFlexProperties();
    this.fxFlexProperties.sm = '0 1 calc(50%-10px)';
    this.fxFlexProperties.md = '0 1 calc(33%-10px)';
    this.fxFlexProperties.gtMd = '0 1 calc(17%-10px)';
    this.fxFlexProperties.order = '2';

    this.fxLayoutProperties = new FxLayoutProperties();
    this.fxLayoutProperties.gap = '20px';
    this.fxLayoutProperties.layout = 'row wrap';
    this.fxLayoutProperties.xs = 'column';

    if (route.snapshot.queryParamMap.get(CONVOCATORIA_ACTION_LINK_KEY)) {
      this.convocatoriaId = Number(route.snapshot.queryParamMap.get(CONVOCATORIA_ACTION_LINK_KEY));
    }
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.setupI18N();
    this.loadForm();

    if (this.convocatoriaId) {
      this.convocatoriaService.findById(this.convocatoriaId).pipe(
        map((convocatoria) => {
          this.formGroup.controls.convocatoria.patchValue(convocatoria);
          this.onSearch();
        }),
        catchError((err) => {
          this.logger.error(err);
          this.processError(err);
          return of({} as IConvocatoria);
        })
      ).subscribe();
    }

    this.suscripciones.push(
      this.cnfService.getLimiteRegistrosExportacionExcel('csp-exp-max-num-registros-excel-solicitud-listado').subscribe(value => {
        this.limiteRegistrosExportacionExcel = value;
      }));
  }

  private loadForm() {
    this.formGroup = new FormGroup({
      convocatoria: new FormControl(undefined),
      estadoSolicitud: new FormControl(''),
      plazoAbierto: new FormControl(false),
      fechaInicioDesde: new FormControl(null),
      fechaInicioHasta: new FormControl(null),
      fechaFinDesde: new FormControl(null),
      fechaFinHasta: new FormControl(null),
      solicitante: new FormControl(undefined),
      nombreSolicitanteExterno: new FormControl(null),
      apellidosSolicitanteExterno: new FormControl(null),
      activo: new FormControl('true'),
      fechaPublicacionConvocatoriaDesde: new FormControl(null),
      fechaPublicacionConvocatoriaHasta: new FormControl(null),
      tituloSolicitud: new FormControl(undefined),
      entidadConvocante: new FormControl(undefined),
      planInvestigacion: new FormControl(undefined),
      entidadFinanciadora: new FormControl(undefined),
      fuenteFinanciacion: new FormControl(undefined),
      palabrasClave: new FormControl(null),
    });

    this.getPlanesInvestigacion();

    this.filter = this.createFilter();
  }

  private setupI18N(): void {
    this.translate.get(
      SOLICITUD_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_BUTTON_NEW,
          { entity: value }
        );
      })
    ).subscribe((value) => this.textoCrear = value);

    this.translate.get(
      SOLICITUD_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_DEACTIVATE,
          { entity: value, ...MSG_PARAMS.GENDER.FEMALE }
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

    this.translate.get(
      SOLICITUD_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_REACTIVE,
          { entity: value, ...MSG_PARAMS.GENDER.FEMALE }
        );
      })
    ).subscribe((value) => this.textoReactivar = value);

    this.translate.get(
      SOLICITUD_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_SUCCESS_REACTIVE,
          { entity: value, ...MSG_PARAMS.GENDER.FEMALE }
        );
      })
    ).subscribe((value) => this.textoSuccessReactivar = value);

    this.translate.get(
      SOLICITUD_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_ERROR_REACTIVE,
          { entity: value, ...MSG_PARAMS.GENDER.FEMALE }
        );
      })
    ).subscribe((value) => this.textoErrorReactivar = value);

    this.translate.get(
      GRUPO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamGrupoEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });
  }

  protected createObservable(reset?: boolean): Observable<SgiRestListResult<ISolicitudListadoData>> {

    return this.solicitudService.findAllTodos(this.getFindOptions(reset)).pipe(
      map(response => {
        return response as SgiRestListResult<ISolicitudListadoData>;
      }),
      switchMap(response => {
        const requestsConvocatoria: Observable<ISolicitudListadoData>[] = [];
        response.items.forEach(solicitud => {
          if (solicitud.estado.estado === Estado.CONCEDIDA
            || solicitud.estado.estado === Estado.CONCEDIDA_PROVISIONAL
            || solicitud.estado.estado === Estado.CONCEDIDA_PROVISIONAL_ALEGADA
            || solicitud.estado.estado === Estado.CONCEDIDA_PROVISIONAL_NO_ALEGADA) {
            solicitud.showCreateGrupo = true;
          } else {
            solicitud.showCreateGrupo = false;
          }
          if (solicitud.convocatoriaId) {
            requestsConvocatoria.push(this.convocatoriaService.findById(solicitud.convocatoriaId).pipe(
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
        if (response.total === 0) {
          return of(response);
        }

        const solicitudes = response.items;
        const personaIdsSolicitantes = new Set<string>(
          solicitudes.filter(solicitud => !!solicitud.solicitante?.id).map((solicitud) => solicitud.solicitante.id)
        );

        if (personaIdsSolicitantes.size === 0) {
          return of(response);
        }

        return this.personaService.findAllByIdIn([...personaIdsSolicitantes]).pipe(
          map((result) => {
            const personas = result.items;

            solicitudes.forEach((solicitud) => {
              if (this.authService.hasAuthorityForAnyUO('CSP-PRO-C')) {
                this.suscripciones.push(this.solicitudService.isPosibleCrearProyecto(solicitud.id).subscribe((value) => {
                  this.mapCrearProyecto.set(solicitud.id, value);
                }));
              }

              solicitud.solicitante = personas.find((persona) => solicitud.solicitante?.id === persona.id) ?? solicitud.solicitante;
              if (this.authService.hasAnyAuthorityForAnyUO(['CSP-SOL-E', 'CSP-SOL-V'])) {
                this.suscripciones.push(this.solicitudService.modificableByUO(solicitud.id).subscribe((value) => {
                  this.mapModificable.set(solicitud.id, value);
                }));

              }
            });
            return response;
          }),
          catchError((error) => {
            this.processError(error);
            return of(response);
          })
        );

      }),
      switchMap(response =>
        from(response.items).pipe(
          mergeMap(solicitud => {
            if (!!!solicitud.solicitante?.id) {
              return this.solicitudService.findSolicitanteExterno(solicitud.id).pipe(
                map(solicitanteExterno => {
                  solicitud.solicitanteExterno = solicitanteExterno;
                  solicitud.nombreSolicitante = `${solicitud.solicitanteExterno?.nombre ?? ''} ${solicitud.solicitanteExterno?.apellidos ?? ''}`;
                  return solicitud;
                })
              );
            }

            solicitud.nombreSolicitante = `${solicitud.solicitante.nombre ?? ''} ${solicitud.solicitante.apellidos ?? ''}`;
            return of(solicitud);
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
    if (this.authService.hasAuthorityForAnyUO('CSP-SOL-R')) {
      this.columnas = [
        'codigoRegistroInterno',
        'codigoExterno',
        'solicitante',
        'externo',
        'titulo',
        'referencia',
        'estado.estado',
        'estado.fechaEstado',
        'activo',
        'acciones'
      ];
    } else {
      this.columnas = [
        'codigoRegistroInterno',
        'codigoExterno',
        'solicitante',
        'externo',
        'titulo',
        'referencia',
        'estado.estado',
        'estado.fechaEstado',
        'acciones'
      ];
    }
  }

  protected loadTable(reset?: boolean): void {
    this.solicitudes$ = this.getObservableLoadTable(reset);
  }

  protected createFilter(): SgiRestFilter {
    const controls = this.formGroup.controls;
    const rsqlFilter = new RSQLSgiRestFilter('convocatoria.id', SgiRestFilterOperator.EQUALS, controls.convocatoria.value?.id?.toString())
      .and('estado.estado', SgiRestFilterOperator.EQUALS, controls.estadoSolicitud.value)
      .and('titulo', SgiRestFilterOperator.LIKE_ICASE, controls.tituloSolicitud.value);
    if (this.busquedaAvanzada) {
      if (controls.plazoAbierto.value) {
        rsqlFilter
          .and('abiertoPlazoPresentacionSolicitud', SgiRestFilterOperator.EQUALS, controls.plazoAbierto.value.toString())
          .and('convocatoria.configuracionSolicitud.fasePresentacionSolicitudes.fechaInicio',
            SgiRestFilterOperator.GREATHER_OR_EQUAL, LuxonUtils.toBackend(controls.fechaInicioDesde.value))
          .and('convocatoria.configuracionSolicitud.fasePresentacionSolicitudes.fechaInicio',
            SgiRestFilterOperator.LOWER_OR_EQUAL, LuxonUtils.toBackend(controls.fechaInicioHasta.value))
          .and('convocatoria.configuracionSolicitud.fasePresentacionSolicitudes.fechaFin',
            SgiRestFilterOperator.GREATHER_OR_EQUAL, LuxonUtils.toBackend(controls.fechaFinDesde.value))
          .and('convocatoria.configuracionSolicitud.fasePresentacionSolicitudes.fechaFin',
            SgiRestFilterOperator.LOWER_OR_EQUAL, LuxonUtils.toBackend(controls.fechaFinHasta.value));
      }
      rsqlFilter
        .and('solicitanteRef', SgiRestFilterOperator.EQUALS, controls.solicitante.value?.id)
        .and('solicitanteExterno.nombre', SgiRestFilterOperator.LIKE_ICASE, controls.nombreSolicitanteExterno.value)
        .and('solicitanteExterno.apellidos', SgiRestFilterOperator.LIKE_ICASE, controls.apellidosSolicitanteExterno.value)
        .and('activo', SgiRestFilterOperator.EQUALS, controls.activo.value)
        .and('convocatoria.fechaPublicacion', SgiRestFilterOperator.GREATHER_OR_EQUAL,
          LuxonUtils.toBackend(controls.fechaPublicacionConvocatoriaDesde.value))
        .and('convocatoria.fechaPublicacion', SgiRestFilterOperator.LOWER_OR_EQUAL,
          LuxonUtils.toBackend(controls.fechaPublicacionConvocatoriaHasta.value))
        .and('convocatoria.entidadesConvocantes.entidadRef', SgiRestFilterOperator.EQUALS, controls.entidadConvocante.value?.id)
        .and('planInvestigacion', SgiRestFilterOperator.EQUALS, controls.planInvestigacion.value?.id?.toString())
        .and('convocatoria.entidadesFinanciadoras.entidadRef', SgiRestFilterOperator.EQUALS, controls.entidadFinanciadora.value?.id)
        .and('convocatoria.entidadesFinanciadoras.fuenteFinanciacion.id',
          SgiRestFilterOperator.EQUALS, controls.fuenteFinanciacion.value?.id?.toString());

      const palabrasClave = controls.palabrasClave.value as string[];
      if (Array.isArray(palabrasClave) && palabrasClave.length > 0) {
        rsqlFilter.and(this.createPalabrasClaveFilter(palabrasClave));
      }
    } else {
      rsqlFilter.and('activo', SgiRestFilterOperator.EQUALS, 'true');
    }

    return rsqlFilter;
  }

  private createPalabrasClaveFilter(palabrasClave: string[]): SgiRestFilter {
    let palabrasClaveFilter: SgiRestFilter;
    palabrasClave.forEach(palabraClave => {
      if (palabrasClaveFilter) {
        palabrasClaveFilter.or('palabrasClave.palabraClaveRef', SgiRestFilterOperator.LIKE_ICASE, palabraClave);
      } else {
        palabrasClaveFilter = new RSQLSgiRestFilter('palabrasClave.palabraClaveRef', SgiRestFilterOperator.LIKE_ICASE, palabraClave);
      }
    });
    return palabrasClaveFilter;
  }

  /**
   * Activar solicitud
   * @param solicitud una solicitud
   */
  activateSolicitud(solicitud: ISolicitud): void {
    const subcription = this.dialogService.showConfirmation(this.textoReactivar).pipe(
      switchMap((accept) => {
        if (accept) {
          return this.solicitudService.reactivar(solicitud.id);
        } else {
          return of();
        }
      })
    ).subscribe(
      () => {
        this.snackBarService.showSuccess(this.textoSuccessReactivar);
        this.loadTable();
      },
      (error) => {
        this.logger.error(error);
        if (error instanceof SgiError) {
          this.processError(error);
        }
        else {
          this.processError(new SgiError(this.textoErrorReactivar));
        }
      }
    );
    this.suscripciones.push(subcription);
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
        if (error instanceof SgiError) {
          this.processError(error);
        }
        else {
          this.processError(new SgiError(this.textoErrorDesactivar));
        }
      }
    );
    this.suscripciones.push(subcription);
  }

  toggleBusquedaAvanzada(): void {
    this.busquedaAvanzada = !this.busquedaAvanzada;
    this.cleanBusquedaAvanzado();
  }

  protected resetFilters(): void {
    super.resetFilters();
    this.cleanBusquedaAvanzado();
  }

  private cleanBusquedaAvanzado(): void {
    this.formGroup.controls.plazoAbierto.setValue(false);
    this.formGroup.controls.fechaInicioDesde.setValue(null);
    this.formGroup.controls.fechaInicioHasta.setValue(null);
    this.formGroup.controls.fechaFinDesde.setValue(null);
    this.formGroup.controls.fechaFinHasta.setValue(null);
    this.formGroup.controls.solicitante.setValue(undefined);
    this.formGroup.controls.activo.setValue('true');
    this.formGroup.controls.fechaPublicacionConvocatoriaDesde.setValue(null);
    this.formGroup.controls.fechaPublicacionConvocatoriaHasta.setValue(null);
    this.formGroup.controls.entidadConvocante.setValue(undefined);
    this.formGroup.controls.entidadFinanciadora.setValue(undefined);
  }

  private getPlanesInvestigacion(): void {
    this.suscripciones.push(
      this.programaService.findAllPlan().subscribe(
        (res) => {
          this.planInvestigaciones$.next(res.items);
        },
        (error) => this.logger.error(error)
      )
    );
  }

  crearProyectoModal(solicitudData: ISolicitudListadoData): void {
    this.suscripciones.push(this.solicitudService.findSolicitudProyecto(solicitudData.id).pipe(
      map(solicitudProyectoDatos => {
        const config = {
          data: {
            solicitud: solicitudData as ISolicitud,
            solicitudProyecto: solicitudProyectoDatos,
            convocatoria: solicitudData.convocatoria,
            nombreSolicitante: solicitudData.nombreSolicitante
          } as ISolicitudCrearProyectoModalData
        };
        const dialogRef = this.matDialog.open(SolicitudCrearProyectoModalComponent, config);
        dialogRef.afterClosed().subscribe(
          (result: IProyecto) => {
            if (result) {
              this.snackBarService.showSuccess(MSG_SUCCESS_CREAR_PROYECTO);
              this.loadTable();
            }
          }
        );
      })
    ).subscribe());
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
    this.matDialog.open(SolicitudListadoExportModalComponent, config);
  }

  /**
   * Apertura de modal de creación de Grupos
   */
  crearGrupoModal(solicitud: ISolicitud): void {
    const data = {
      solicitud
    } as ISolicitudGrupo;

    const config = {
      data
    };

    this.matDialog.open(SolicitudGrupoModalComponent, config).afterClosed()
      .pipe(
        filter(solicitudGrupo => solicitudGrupo)
      ).subscribe(
        (result) => {
          if (result) {
            this.snackBarService.showSuccess(MSG_SUCCESS_CREAR_GRUPO);
            this.loadTable();
          }
        }
      );
  }

}
