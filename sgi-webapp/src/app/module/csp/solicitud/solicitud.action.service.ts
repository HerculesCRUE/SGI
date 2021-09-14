import { Injectable } from '@angular/core';
import { ErrorStateMatcher } from '@angular/material/core';
import { ActivatedRoute } from '@angular/router';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { FormularioSolicitud } from '@core/enums/formulario-solicitud';
import { MSG_PARAMS } from '@core/i18n';
import { IConvocatoria } from '@core/models/csp/convocatoria';
import { IConvocatoriaRequisitoEquipo } from '@core/models/csp/convocatoria-requisito-equipo';
import { IConvocatoriaRequisitoIP } from '@core/models/csp/convocatoria-requisito-ip';
import { Estado, IEstadoSolicitud } from '@core/models/csp/estado-solicitud';
import { IRequisitoEquipoCategoriaProfesional } from '@core/models/csp/requisito-equipo-categoria-profesional';
import { ISolicitud } from '@core/models/csp/solicitud';
import { ISolicitudProyecto, TipoPresupuesto } from '@core/models/csp/solicitud-proyecto';
import { ISolicitudProyectoSocio } from '@core/models/csp/solicitud-proyecto-socio';
import { IPersona } from '@core/models/sgp/persona';
import { IVinculacion } from '@core/models/sgp/vinculacion';
import { ActionService } from '@core/services/action-service';
import { ConfiguracionSolicitudService } from '@core/services/csp/configuracion-solicitud.service';
import { ConvocatoriaRequisitoEquipoService } from '@core/services/csp/convocatoria-requisito-equipo.service';
import { ConvocatoriaRequisitoIPService } from '@core/services/csp/convocatoria-requisito-ip.service';
import { ConvocatoriaService } from '@core/services/csp/convocatoria.service';
import { RolProyectoService } from '@core/services/csp/rol-proyecto.service';
import { SolicitudDocumentoService } from '@core/services/csp/solicitud-documento.service';
import { SolicitudHitoService } from '@core/services/csp/solicitud-hito.service';
import { SolicitudModalidadService } from '@core/services/csp/solicitud-modalidad.service';
import { SolicitudProyectoAreaConocimientoService } from '@core/services/csp/solicitud-proyecto-area-conocimiento.service';
import { SolicitudProyectoClasificacionService } from '@core/services/csp/solicitud-proyecto-clasificacion.service';
import { SolicitudProyectoEntidadFinanciadoraAjenaService } from '@core/services/csp/solicitud-proyecto-entidad-financiadora-ajena.service';
import { SolicitudProyectoEquipoService } from '@core/services/csp/solicitud-proyecto-equipo.service';
import { SolicitudProyectoPresupuestoService } from '@core/services/csp/solicitud-proyecto-presupuesto.service';
import { SolicitudProyectoResponsableEconomicoService } from '@core/services/csp/solicitud-proyecto-responsable-economico/solicitud-proyecto-responsable-economico.service';
import { SolicitudProyectoSocioService } from '@core/services/csp/solicitud-proyecto-socio.service';
import { SolicitudProyectoService } from '@core/services/csp/solicitud-proyecto.service';
import { SolicitudService } from '@core/services/csp/solicitud.service';
import { UnidadGestionService } from '@core/services/csp/unidad-gestion.service';
import { DialogService } from '@core/services/dialog.service';
import { ChecklistService } from '@core/services/eti/checklist/checklist.service';
import { FormlyService } from '@core/services/eti/formly/formly.service';
import { EmpresaService } from '@core/services/sgemp/empresa.service';
import { AreaConocimientoService } from '@core/services/sgo/area-conocimiento.service';
import { ClasificacionService } from '@core/services/sgo/clasificacion.service';
import { DatosAcademicosService } from '@core/services/sgp/datos-academicos.service';
import { PersonaService } from '@core/services/sgp/persona.service';
import { VinculacionService } from '@core/services/sgp/vinculacion.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { SgiAuthService } from '@sgi/framework/auth';
import { DateTime } from 'luxon';
import { NGXLogger } from 'ngx-logger';
import { BehaviorSubject, Observable, of, Subject, throwError } from 'rxjs';
import { map, switchMap, tap } from 'rxjs/operators';
import { CSP_ROUTE_NAMES } from '../csp-route-names';
import { CONVOCATORIA_ID_KEY } from './solicitud-crear/solicitud-crear.guard';
import { SOLICITUD_DATA_KEY } from './solicitud-data.resolver';
import { SolicitudAutoevaluacionFragment } from './solicitud-formulario/solicitud-autoevaluacion/solicitud-autoevaluacion.fragment';
import { SolicitudDatosGeneralesFragment } from './solicitud-formulario/solicitud-datos-generales/solicitud-datos-generales.fragment';
import { SolicitudDocumentosFragment } from './solicitud-formulario/solicitud-documentos/solicitud-documentos.fragment';
import { SolicitudEquipoProyectoFragment } from './solicitud-formulario/solicitud-equipo-proyecto/solicitud-equipo-proyecto.fragment';
import { SolicitudHistoricoEstadosFragment } from './solicitud-formulario/solicitud-historico-estados/solicitud-historico-estados.fragment';
import { SolicitudHitosFragment } from './solicitud-formulario/solicitud-hitos/solicitud-hitos.fragment';
import { SolicitudProyectoAreaConocimientoFragment } from './solicitud-formulario/solicitud-proyecto-area-conocimiento/solicitud-proyecto-area-conocimiento.fragment';
import { SolicitudProyectoClasificacionesFragment } from './solicitud-formulario/solicitud-proyecto-clasificaciones/solicitud-proyecto-clasificaciones.fragment';
import { SolicitudProyectoEntidadesFinanciadorasFragment } from './solicitud-formulario/solicitud-proyecto-entidades-financiadoras/solicitud-proyecto-entidades-financiadoras.fragment';
import { SolicitudProyectoFichaGeneralFragment } from './solicitud-formulario/solicitud-proyecto-ficha-general/solicitud-proyecto-ficha-general.fragment';
import { SolicitudProyectoPresupuestoEntidadesFragment } from './solicitud-formulario/solicitud-proyecto-presupuesto-entidades/solicitud-proyecto-presupuesto-entidades.fragment';
import { SolicitudProyectoPresupuestoGlobalFragment } from './solicitud-formulario/solicitud-proyecto-presupuesto-global/solicitud-proyecto-presupuesto-global.fragment';
import { SolicitudProyectoResponsableEconomicoFragment } from './solicitud-formulario/solicitud-proyecto-responsable-economico/solicitud-proyecto-responsable-economico.fragment';
import { SolicitudProyectoSocioFragment } from './solicitud-formulario/solicitud-proyecto-socio/solicitud-proyecto-socio.fragment';

const MSG_CONVOCATORIAS = marker('csp.convocatoria');
const MSG_SAVE_REQUISITOS_INVESTIGADOR = marker('msg.save.solicitud.requisitos-investigador');

export interface ISolicitudData {
  readonly: boolean;
  solicitud: ISolicitud;
  hasSolicitudProyecto: boolean;
  hasPopulatedPeriodosSocios: boolean;
  solicitudProyecto: ISolicitudProyecto;
  hasAnySolicitudProyectoSocioWithRolCoordinador: boolean;
}

@Injectable()
export class SolicitudActionService extends ActionService {

  public readonly FRAGMENT = {
    DATOS_GENERALES: 'datosGenerales',
    HISTORICO_ESTADOS: 'historicoEstados',
    DOCUMENTOS: 'documentos',
    PROYECTO_DATOS: 'proyectoDatos',
    PROYECTO_AREA_CONOCIMIENTO: 'areaConocimiento',
    HITOS: 'hitos',
    EQUIPO_PROYECTO: 'equipoProyecto',
    SOCIOS: 'socios',
    ENTIDADES_FINANCIADORAS: 'entidadesFinanciadoras',
    DESGLOSE_PRESUPUESTO_GLOBAL: 'desglosePresupuestoGlobal',
    DESGLOSE_PRESUPUESTO_ENTIDADES: 'desglosePresupuestoEntidades',
    CLASIFICACIONES: 'clasificaciones',
    RESPONSABLE_ECONOMICO: 'responsable-economico',
    AUTOEVALUACION: 'autoevaluacion'
  };

  private datosGenerales: SolicitudDatosGeneralesFragment;
  private historicoEstado: SolicitudHistoricoEstadosFragment;
  private documentos: SolicitudDocumentosFragment;
  private proyectoDatos: SolicitudProyectoFichaGeneralFragment;
  private areaConocimiento: SolicitudProyectoAreaConocimientoFragment;
  private hitos: SolicitudHitosFragment;
  private equipoProyecto: SolicitudEquipoProyectoFragment;
  private socio: SolicitudProyectoSocioFragment;
  private entidadesFinanciadoras: SolicitudProyectoEntidadesFinanciadorasFragment;
  private desglosePresupuestoGlobal: SolicitudProyectoPresupuestoGlobalFragment;
  private desglosePresupuestoEntidades: SolicitudProyectoPresupuestoEntidadesFragment;
  private clasificaciones: SolicitudProyectoClasificacionesFragment;
  private responsableEconomico: SolicitudProyectoResponsableEconomicoFragment;
  private autoevaluacion: SolicitudAutoevaluacionFragment;
  public readonly isInvestigador: boolean;

  readonly showSocios$: Subject<boolean> = new BehaviorSubject(false);
  readonly showHitos$: Subject<boolean> = new BehaviorSubject<boolean>(false);
  readonly showDesglosePresupuestoGlobal$: Subject<boolean> = new BehaviorSubject<boolean>(false);
  readonly showDesglosePresupuestoEntidad$: Subject<boolean> = new BehaviorSubject<boolean>(false);
  readonly datosProyectoComplete$: Subject<boolean> = new BehaviorSubject<boolean>(false);
  readonly showAlertNotSocioCoordinadorExist$ = new BehaviorSubject<boolean>(false);
  readonly hasAnySolicitudProyectoSocioWithRolCoordinador$ = new BehaviorSubject<boolean>(false);

  private readonly data: ISolicitudData;
  private convocatoria: IConvocatoria;

  get formularioSolicitud(): FormularioSolicitud {
    return this.datosGenerales.getValue().formularioSolicitud;
  }

  get duracionProyecto(): number {
    return this.proyectoDatos.getValue().duracion;
  }

  get estado(): Estado {
    return this.datosGenerales.getValue().estado?.estado;
  }

  get convocatoriaId(): number {
    return this.convocatoria?.id;
  }

  get modeloEjecucionId(): number {
    return this.convocatoria?.modeloEjecucion?.id;
  }

  get solicitante(): IPersona {
    return this.datosGenerales.getValue().solicitante;
  }

  get readonly(): boolean {
    return this.data?.readonly ?? false;
  }

  constructor(
    logger: NGXLogger,
    route: ActivatedRoute,
    private solicitudService: SolicitudService,
    configuracionSolicitudService: ConfiguracionSolicitudService,
    private convocatoriaService: ConvocatoriaService,
    empresaService: EmpresaService,
    personaService: PersonaService,
    solicitudModalidadService: SolicitudModalidadService,
    solicitudHitoService: SolicitudHitoService,
    unidadGestionService: UnidadGestionService,
    solicitudDocumentoService: SolicitudDocumentoService,
    protected solicitudProyectoService: SolicitudProyectoService,
    solicitudProyectoEquipoService: SolicitudProyectoEquipoService,
    solicitudProyectoSocioService: SolicitudProyectoSocioService,
    solicitudEntidadFinanciadoraService: SolicitudProyectoEntidadFinanciadoraAjenaService,
    solicitudProyectoPresupuestoService: SolicitudProyectoPresupuestoService,
    solicitudProyectoClasificacionService: SolicitudProyectoClasificacionService,
    clasificacionService: ClasificacionService,
    authService: SgiAuthService,
    solicitudProyectoAreaConocimiento: SolicitudProyectoAreaConocimientoService,
    areaConocimientoService: AreaConocimientoService,
    solicitudProyectoResponsableEconomicoService: SolicitudProyectoResponsableEconomicoService,
    formlyService: FormlyService,
    checklistService: ChecklistService,
    private datosAcademicosService: DatosAcademicosService,
    private vinculacionService: VinculacionService,
    private convocatoriaRequisitoIpService: ConvocatoriaRequisitoIPService,
    private convocatoriaRquisitoEquipoService: ConvocatoriaRequisitoEquipoService,
    private dialogService: DialogService,
    rolProyectoService: RolProyectoService
  ) {
    super();

    if (route.snapshot.data[SOLICITUD_DATA_KEY]) {
      this.data = route.snapshot.data[SOLICITUD_DATA_KEY];
      this.enableEdit();
      this.datosProyectoComplete$.next(this.data.hasSolicitudProyecto);
      if (this.data.solicitud.convocatoriaId) {
        this.addConvocatoriaLink(this.data.solicitud.convocatoriaId);
      }
    }

    this.isInvestigador = authService.hasAuthority('CSP-SOL-INV-C');
    const idConvocatoria = history.state[CONVOCATORIA_ID_KEY];

    this.datosGenerales = new SolicitudDatosGeneralesFragment(
      logger,
      this.data?.solicitud?.id,
      solicitudService,
      configuracionSolicitudService,
      convocatoriaService,
      empresaService,
      personaService,
      solicitudModalidadService,
      unidadGestionService,
      authService,
      this.readonly,
      this.isInvestigador
    );

    if (this.isInvestigador && idConvocatoria) {
      this.loadConvocatoria(idConvocatoria);
    }

    this.documentos = new SolicitudDocumentosFragment(logger, this.data?.solicitud?.id, this.data?.solicitud?.convocatoriaId,
      configuracionSolicitudService, solicitudService, solicitudDocumentoService, this.readonly);
    this.areaConocimiento = new SolicitudProyectoAreaConocimientoFragment(this.data?.solicitud?.id,
      solicitudProyectoAreaConocimiento, solicitudService, areaConocimientoService, this.readonly);
    this.hitos = new SolicitudHitosFragment(this.data?.solicitud?.id, solicitudHitoService, solicitudService, this.readonly);
    this.historicoEstado = new SolicitudHistoricoEstadosFragment(this.data?.solicitud?.id, solicitudService, this.readonly);
    this.proyectoDatos = new SolicitudProyectoFichaGeneralFragment(logger, this.data?.solicitud?.id, solicitudService,
      solicitudProyectoService, convocatoriaService, this.readonly, this.data?.solicitud.convocatoriaId,
      this.hasAnySolicitudProyectoSocioWithRolCoordinador$, this.data?.hasPopulatedPeriodosSocios);
    this.equipoProyecto = new SolicitudEquipoProyectoFragment(this.data?.solicitud?.id, solicitudService,
      solicitudProyectoEquipoService, this, rolProyectoService, this.readonly);
    this.socio = new SolicitudProyectoSocioFragment(this.data?.solicitud?.id, solicitudService,
      solicitudProyectoSocioService, empresaService, this.readonly);
    this.entidadesFinanciadoras = new SolicitudProyectoEntidadesFinanciadorasFragment(this.data?.solicitud?.id, solicitudService,
      solicitudEntidadFinanciadoraService, empresaService, this.readonly);
    this.desglosePresupuestoGlobal = new SolicitudProyectoPresupuestoGlobalFragment(this.data?.solicitud?.id, solicitudService,
      solicitudProyectoPresupuestoService, empresaService, solicitudProyectoService, this.readonly);
    this.desglosePresupuestoEntidades = new SolicitudProyectoPresupuestoEntidadesFragment(this.data?.solicitud?.id,
      this.data?.solicitud?.convocatoriaId, convocatoriaService, solicitudService, empresaService, solicitudProyectoService, this.readonly);
    this.clasificaciones = new SolicitudProyectoClasificacionesFragment(this.data?.solicitud?.id, solicitudProyectoClasificacionService,
      solicitudService, clasificacionService, this.readonly);
    this.responsableEconomico = new SolicitudProyectoResponsableEconomicoFragment(this.data?.solicitud?.id, solicitudService,
      solicitudProyectoResponsableEconomicoService, personaService, this.readonly);
    this.autoevaluacion = new SolicitudAutoevaluacionFragment(formlyService, checklistService, authService);

    this.addFragment(this.FRAGMENT.DATOS_GENERALES, this.datosGenerales);

    this.subscriptions.push(this.datosGenerales.convocatoria$.subscribe(
      (value) => {
        this.convocatoria = value;
      }
    ));


    // Por ahora solo está implementado para investigador la pestaña datos generales
    if (this.isEdit() && !this.isInvestigador) {
      this.addFragment(this.FRAGMENT.HITOS, this.hitos);
      this.addFragment(this.FRAGMENT.HISTORICO_ESTADOS, this.historicoEstado);
      this.addFragment(this.FRAGMENT.DOCUMENTOS, this.documentos);

      if (this.data.solicitud.formularioSolicitud === FormularioSolicitud.ESTANDAR) {
        this.addFragment(this.FRAGMENT.PROYECTO_DATOS, this.proyectoDatos);
        this.addFragment(this.FRAGMENT.EQUIPO_PROYECTO, this.equipoProyecto);
        this.addFragment(this.FRAGMENT.SOCIOS, this.socio);
        this.addFragment(this.FRAGMENT.ENTIDADES_FINANCIADORAS, this.entidadesFinanciadoras);
        this.addFragment(this.FRAGMENT.DESGLOSE_PRESUPUESTO_GLOBAL, this.desglosePresupuestoGlobal);
        this.addFragment(this.FRAGMENT.DESGLOSE_PRESUPUESTO_ENTIDADES, this.desglosePresupuestoEntidades);
        this.addFragment(this.FRAGMENT.CLASIFICACIONES, this.clasificaciones);
        this.addFragment(this.FRAGMENT.PROYECTO_AREA_CONOCIMIENTO, this.areaConocimiento);
        this.addFragment(this.FRAGMENT.RESPONSABLE_ECONOMICO, this.responsableEconomico);
        this.addFragment(this.FRAGMENT.AUTOEVALUACION, this.autoevaluacion);
      }

      if (this.data.solicitud.formularioSolicitud === FormularioSolicitud.ESTANDAR) {
        this.subscriptions.push(
          solicitudService.hasConvocatoriaSGI(this.data.solicitud.id).subscribe((hasConvocatoriaSgi) => {
            if (hasConvocatoriaSgi) {
              this.showHitos$.next(true);
            }
          })
        );

        this.subscriptions.push(this.proyectoDatos.coordinado$.subscribe(
          (value: boolean) => {
            this.showSocios$.next(value);
          }
        ));

        this.subscriptions.push(this.proyectoDatos.tipoDesglosePresupuesto$.subscribe(
          (value) => {
            this.showDesglosePresupuestoEntidad$.next(value !== TipoPresupuesto.GLOBAL);
            this.showDesglosePresupuestoGlobal$.next(value === TipoPresupuesto.GLOBAL);
            this.desglosePresupuestoEntidades.tipoPresupuesto$.next(value);
          }
        ));

        this.subscriptions.push(this.desglosePresupuestoGlobal.partidasGastos$.subscribe((value) => {
          const rowTableData = value.length > 0;
          this.proyectoDatos.disableTipoDesglosePresupuesto(rowTableData);
        }));

        this.subscriptions.push(this.socio.proyectoSocios$.subscribe((value) => {
          const rowTableData = value.length > 0;
          this.proyectoDatos.disableProyectoCoordinadoIfAnySocioExists(rowTableData);
        }));

        this.subscriptions.push(this.proyectoDatos.status$.subscribe(
          (status) => {
            if (this.proyectoDatos.isInitialized() && !Boolean(this.proyectoDatos.getValue()?.id)) {
              if (status.changes && status.errors) {
                this.datosProyectoComplete$.next(false);
              }
              else if (status.changes && !status.errors) {
                this.datosProyectoComplete$.next(true);
                this.equipoProyecto.initialize();
              }
            }
          }
        ));

        this.subscriptions.push(this.proyectoDatos.initialized$.subscribe(
          (value) => {
            if (value) {
              this.autoevaluacion.solicitudProyectoData$.next({
                checklistRef: this.proyectoDatos.getValue().checklistRef,
                readonly: this.readonly || !!this.proyectoDatos.getValue().peticionEvaluacionRef
              });
            }
          }
        ));

        this.subscriptions.push(this.socio.proyectoSocios$.subscribe(
          (proyectoSocios) => {
            this.onSolicitudProyectoSocioListChangeHandle(proyectoSocios);
          }
        ));
      }

      // Forzamos la inicialización de los datos principales
      this.datosGenerales.initialize();

      if (this.data.solicitud.formularioSolicitud === FormularioSolicitud.ESTANDAR) {
        this.proyectoDatos.initialize();
      }
    } else if (this.isEdit() && this.isInvestigador) {
      this.subscriptions.push(this.datosGenerales.convocatoria$.subscribe(
        (value) => {
          this.convocatoria = value;
        }
      ));

      // Forzamos la inicialización de los datos principales
      this.datosGenerales.initialize();
    }
    this.hasAnySolicitudProyectoSocioWithRolCoordinador$.next(this.data?.hasAnySolicitudProyectoSocioWithRolCoordinador);
  }

  private addConvocatoriaLink(idConvocatoria: number): void {
    this.addActionLink({
      title: MSG_CONVOCATORIAS,
      titleParams: MSG_PARAMS.CARDINALIRY.SINGULAR,
      routerLink: ['../..', CSP_ROUTE_NAMES.CONVOCATORIA, idConvocatoria.toString()]
    });
  }

  saveOrUpdate(): Observable<void> {
    this.performChecks(true);
    if (this.hasErrors()) {
      return throwError('Errores');
    }

    return this.validateRequisitosConvocatoria().pipe(
      switchMap((errors) => {
        if (errors) {
          return this.dialogService.showConfirmation(MSG_SAVE_REQUISITOS_INVESTIGADOR).pipe(
            switchMap((aceptado) => {
              if (aceptado) {
                return this.saveOrUpdateSolicitud();
              }
            }));
        }
        return this.saveOrUpdateSolicitud();
      })
    );

  }

  /**
   * Cambio de estado a **Presentada** desde:
   * - **Borrador**
   */
  cambiarEstado(estadoNuevo: IEstadoSolicitud): Observable<IEstadoSolicitud> {
    return this.solicitudService.cambiarEstado(this.datosGenerales.getKey() as number, estadoNuevo);
  }

  private saveOrUpdateSolicitud() {
    if (this.isEdit()) {
      let cascade = of(void 0);
      if (this.datosGenerales.hasChanges()) {
        cascade = cascade.pipe(
          switchMap(() => this.datosGenerales.saveOrUpdate().pipe(tap(() => this.datosGenerales.refreshInitialState(true))))
        );
      }
      if (this.autoevaluacion.hasChanges() && !this.autoevaluacion.isEdit()) {
        if (this.proyectoDatos.hasChanges()) {
          cascade = cascade.pipe(
            switchMap(() => this.autoevaluacion.saveOrUpdate().pipe(
              tap(() => this.autoevaluacion.refreshInitialState(true)),
              switchMap(checklistRef => {
                this.proyectoDatos.setChecklistRef(checklistRef);
                return this.proyectoDatos.saveOrUpdate().pipe(tap(() => this.proyectoDatos.refreshInitialState(true)));
              })
            ))
          );
        }
        else {
          cascade = cascade.pipe(
            switchMap(() => this.autoevaluacion.saveOrUpdate().pipe(
              tap((checklistRef) => {
                this.autoevaluacion.refreshInitialState(true);
                this.proyectoDatos.setChecklistRef(checklistRef);
              }),
              switchMap(checklistRef => {
                this.proyectoDatos.setChecklistRef(checklistRef);
                return this.proyectoDatos.saveOrUpdate().pipe(tap(() => this.proyectoDatos.refreshInitialState(true)));
              })
            ))
          );
        }
      }
      else if (this.autoevaluacion.hasChanges() && this.autoevaluacion.isEdit()) {
        cascade = cascade.pipe(
          switchMap(() => this.autoevaluacion.saveOrUpdate().pipe(
            tap((checklistRef) => {
              this.autoevaluacion.refreshInitialState(true);
              this.proyectoDatos.setChecklistRef(checklistRef);
            })
          ))
        );
        if (this.proyectoDatos.hasChanges()) {
          cascade = cascade.pipe(
            switchMap(() => this.proyectoDatos.saveOrUpdate().pipe(tap(() => this.proyectoDatos.refreshInitialState(true))))
          );
        }
      }
      else {
        if (this.proyectoDatos.hasChanges()) {
          cascade = cascade.pipe(
            switchMap(() => this.proyectoDatos.saveOrUpdate().pipe(tap(() => this.proyectoDatos.refreshInitialState(true))))
          );
        }
      }

      if (this.equipoProyecto.hasChanges()) {
        cascade = cascade.pipe(
          switchMap(() => this.equipoProyecto.saveOrUpdate().pipe(tap(() => this.equipoProyecto.refreshInitialState(true))))
        );
      }
      return cascade.pipe(
        switchMap(() => super.saveOrUpdate())
      );
    } else {
      let cascade = of(void 0);
      if (this.datosGenerales.hasChanges()) {
        cascade = cascade.pipe(
          switchMap(() => this.datosGenerales.saveOrUpdate().pipe(
            tap((key) => {
              this.datosGenerales.refreshInitialState(true);
              if (typeof key === 'string' || typeof key === 'number') {
                this.onKeyChange(key);
              }
            })
          ))
        );
      }
      if (this.proyectoDatos.hasChanges()) {
        cascade = cascade.pipe(
          switchMap(() => this.proyectoDatos.saveOrUpdate().pipe(tap(() => this.proyectoDatos.refreshInitialState(true))))
        );
      }
      if (this.equipoProyecto.hasChanges()) {
        cascade = cascade.pipe(
          switchMap(() => this.equipoProyecto.saveOrUpdate().pipe(tap(() => this.equipoProyecto.refreshInitialState(true))))
        );
      }
      return cascade.pipe(
        switchMap(() => super.saveOrUpdate())
      );
    }
  }

  private validateRequisitosConvocatoria(): Observable<boolean> {
    if (this.convocatoriaId) {
      let fechaObtencion: DateTime;
      let vinculacion: IVinculacion;
      let categoriasProfesionalesConvocatoria: IRequisitoEquipoCategoriaProfesional[];
      return this.convocatoriaService.findNivelesAcademicos(this.convocatoriaId).pipe(
        switchMap((nivelesAcademicos) => {
          if (nivelesAcademicos.length > 0) {
            return this.datosAcademicosService.findByPersonaId(this.solicitante.id).pipe(
              switchMap((datosAcademicos) => {
                if (datosAcademicos) {
                  fechaObtencion = datosAcademicos.fechaObtencion;
                  const nivelAcademicoSolicitante =
                    nivelesAcademicos.filter(nivelAcademico => nivelAcademico?.nivelAcademico?.id === datosAcademicos.nivelAcademico.id);
                  if (nivelAcademicoSolicitante.length > 0) {
                    return this.convocatoriaRequisitoIpService.getRequisitoIPConvocatoria
                      (this.convocatoriaId);
                  }
                } else {
                  return of(null);
                }
              }),
              map((convocatoriaRequisitoIp: IConvocatoriaRequisitoIP) => {
                if
                  (convocatoriaRequisitoIp?.fechaMaximaNivelAcademico ||
                  convocatoriaRequisitoIp?.fechaMinimaNivelAcademico) {
                  if (!fechaObtencion) {
                    return false;
                  } else if (fechaObtencion.startOf('day') >
                    convocatoriaRequisitoIp.fechaMaximaNivelAcademico?.startOf('day')) {
                    return false;
                  } else if (fechaObtencion.startOf('day') <
                    convocatoriaRequisitoIp.fechaMinimaNivelAcademico?.startOf('day')) {
                    return false;
                  } else {
                    return true;
                  }
                } else {
                  return false;
                }
              })
            );
          }
          return of(false);
        }),
        switchMap((erroresNivelesAcademicos: boolean) => {
          if (!erroresNivelesAcademicos) {
            return this.convocatoriaService.findCategoriasProfesionales(this.convocatoriaId).pipe(
              switchMap((categoriasProfesionales) => {
                if (categoriasProfesionales.length > 0) {
                  categoriasProfesionalesConvocatoria = categoriasProfesionales;
                  return this.vinculacionService.findByPersonaId(this.solicitante.id);
                } else {
                  return of(null);
                }
              }),
              switchMap((vinculacionSolicitante: IVinculacion) => {
                const categoriaProfesionalSolicitante =
                  categoriasProfesionalesConvocatoria?.filter(categoriaProfesional => categoriaProfesional.categoriaProfesional.id ===
                    vinculacionSolicitante?.categoriaProfesional.id);
                vinculacion = vinculacionSolicitante;
                if (categoriaProfesionalSolicitante?.length > 0) {
                  return this.convocatoriaRquisitoEquipoService.findById(this.convocatoriaId);

                } else {
                  return of(null);
                }
              }),
              map((convocatoriaRequisitoEquipo: IConvocatoriaRequisitoEquipo) => {

                if (convocatoriaRequisitoEquipo?.sexo.id && this.solicitante?.sexo.nombre !== convocatoriaRequisitoEquipo?.sexo.nombre) {
                  return true;
                }

                if (categoriasProfesionalesConvocatoria?.length !== 0 && !convocatoriaRequisitoEquipo?.vinculacionUniversidad) {
                  return true;
                }

                if (convocatoriaRequisitoEquipo?.fechaMaximaCategoriaProfesional ||
                  convocatoriaRequisitoEquipo?.fechaMinimaCategoriaProfesional) {
                  if (!vinculacion.id) {
                    return false;
                  } else if (vinculacion.fechaObtencionCategoria >
                    convocatoriaRequisitoEquipo.fechaMaximaCategoriaProfesional) {
                    return false;
                  } else if (vinculacion.fechaObtencionCategoria <
                    convocatoriaRequisitoEquipo.fechaMinimaCategoriaProfesional) {
                    return false;
                  } else {
                    return true;
                  }
                } else {
                  return false;
                }
              })
            );
          }
          return of(erroresNivelesAcademicos);
        })
      );
    }
  }

  private loadConvocatoria(id: number): void {
    if (id) {
      this.convocatoriaService.findById(id).subscribe(convocatoria => {
        this.datosGenerales.setDatosConvocatoria(convocatoria);
      });
    }
  }

  private onSolicitudProyectoSocioListChangeHandle(proyectoSocios: StatusWrapper<ISolicitudProyectoSocio>[]): void {

    let needShow = false;
    if (this.proyectoDatos.getFormGroup()?.controls?.coordinado.value
      && this.proyectoDatos.getFormGroup()?.controls?.coordinadorExterno.value) {
      const socioCoordinador = proyectoSocios.find((socio: StatusWrapper<ISolicitudProyectoSocio>) => socio.value.rolSocio.coordinador);

      if (socioCoordinador) {
        needShow = false;
      } else {
        needShow = true;
      }
    } else {
      needShow = false;
    }
    this.showAlertNotSocioCoordinadorExist$.next(needShow);
  }
}
