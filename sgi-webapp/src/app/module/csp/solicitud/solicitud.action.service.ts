import { T } from '@angular/cdk/keycodes';
import { Injectable } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { FormularioSolicitud } from '@core/enums/formulario-solicitud';
import { VALIDACION_REQUISITOS_EQUIPO_IP_MAP } from '@core/enums/validaciones-requisitos-equipo-ip';
import { MSG_PARAMS } from '@core/i18n';
import { IConvocatoria } from '@core/models/csp/convocatoria';
import { Estado, IEstadoSolicitud } from '@core/models/csp/estado-solicitud';
import { IProyecto } from '@core/models/csp/proyecto';
import { ISolicitud } from '@core/models/csp/solicitud';
import { ISolicitudProyecto, TipoPresupuesto } from '@core/models/csp/solicitud-proyecto';
import { ISolicitudProyectoSocio } from '@core/models/csp/solicitud-proyecto-socio';
import { IPersona } from '@core/models/sgp/persona';
import { Module } from '@core/module';
import { ActionService } from '@core/services/action-service';
import { ConfiguracionSolicitudService } from '@core/services/csp/configuracion-solicitud.service';
import { ConvocatoriaRequisitoEquipoService } from '@core/services/csp/convocatoria-requisito-equipo.service';
import { ConvocatoriaRequisitoIPService } from '@core/services/csp/convocatoria-requisito-ip.service';
import { ConvocatoriaService } from '@core/services/csp/convocatoria.service';
import { ProyectoService } from '@core/services/csp/proyecto.service';
import { RolProyectoService } from '@core/services/csp/rol-proyecto/rol-proyecto.service';
import { SolicitanteExternoService } from '@core/services/csp/solicitante-externo/solicitante-externo.service';
import { SolicitudDocumentoService } from '@core/services/csp/solicitud-documento.service';
import { SolicitudGrupoService } from '@core/services/csp/solicitud-grupo/solicitud-grupo.service';
import { SolicitudHitoService } from '@core/services/csp/solicitud-hito/solicitud-hito.service';
import { SolicitudModalidadService } from '@core/services/csp/solicitud-modalidad.service';
import { SolicitudProyectoAreaConocimientoService } from '@core/services/csp/solicitud-proyecto-area-conocimiento.service';
import { SolicitudProyectoClasificacionService } from '@core/services/csp/solicitud-proyecto-clasificacion.service';
import { SolicitudProyectoEntidadFinanciadoraAjenaService } from '@core/services/csp/solicitud-proyecto-entidad-financiadora-ajena.service';
import { SolicitudProyectoEquipoService } from '@core/services/csp/solicitud-proyecto-equipo.service';
import { SolicitudProyectoPresupuestoService } from '@core/services/csp/solicitud-proyecto-presupuesto.service';
import { SolicitudProyectoResponsableEconomicoService } from '@core/services/csp/solicitud-proyecto-responsable-economico/solicitud-proyecto-responsable-economico.service';
import { SolicitudProyectoSocioService } from '@core/services/csp/solicitud-proyecto-socio.service';
import { SolicitudProyectoService } from '@core/services/csp/solicitud-proyecto.service';
import { SolicitudRrhhRequisitoCategoriaService } from '@core/services/csp/solicitud-rrhh-requisito-categoria/solicitud-rrhh-requisito-categoria.service';
import { SolicitudRrhhRequisitoNivelAcademicoService } from '@core/services/csp/solicitud-rrhh-requisito-nivel-academico/solicitud-rrhh-requisito-nivel-academico.service';
import { SolicitudRrhhService } from '@core/services/csp/solicitud-rrhh/solicitud-rrhh.service';
import { SolicitudService } from '@core/services/csp/solicitud.service';
import { UnidadGestionService } from '@core/services/csp/unidad-gestion.service';
import { DialogService } from '@core/services/dialog.service';
import { ChecklistService } from '@core/services/eti/checklist/checklist.service';
import { FormlyService } from '@core/services/eti/formly/formly.service';
import { DocumentoService } from '@core/services/sgdoc/documento.service';
import { EmpresaService } from '@core/services/sgemp/empresa.service';
import { AreaConocimientoService } from '@core/services/sgo/area-conocimiento.service';
import { ClasificacionService } from '@core/services/sgo/clasificacion.service';
import { PalabraClaveService } from '@core/services/sgo/palabra-clave.service';
import { CategoriaProfesionalService } from '@core/services/sgp/categoria-profesional.service';
import { DatosAcademicosService } from '@core/services/sgp/datos-academicos.service';
import { DatosContactoService } from '@core/services/sgp/datos-contacto/datos-contacto.service';
import { DatosPersonalesService } from '@core/services/sgp/datos-personales.service';
import { NivelAcademicosService } from '@core/services/sgp/nivel-academico.service';
import { PersonaService } from '@core/services/sgp/persona.service';
import { VinculacionService } from '@core/services/sgp/vinculacion/vinculacion.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { TranslateService } from '@ngx-translate/core';
import { SgiAuthService } from '@sgi/framework/auth';
import { NGXLogger } from 'ngx-logger';
import { BehaviorSubject, NEVER, Observable, of, Subject, throwError } from 'rxjs';
import { map, switchMap, tap } from 'rxjs/operators';
import { CSP_ROUTE_NAMES } from '../csp-route-names';
import { PROYECTO_ROUTE_NAMES } from '../proyecto/proyecto-route-names';
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
import { SolicitudRrhhMemoriaFragment } from './solicitud-formulario/solicitud-rrhh-memoria/solicitud-rrhh-memoria.fragment';
import { SolicitudRrhhRequisitosConvocatoriaFragment } from './solicitud-formulario/solicitud-rrhh-requisitos-convocatoria/solicitud-rrhh-requisitos-convocatoria.fragment';
import { SolicitudRrhhSolitanteFragment } from './solicitud-formulario/solicitud-rrhh-solicitante/solicitud-rrhh-solicitante.fragment';
import { SolicitudRrhhTutorFragment } from './solicitud-formulario/solicitud-rrhh-tutor/solicitud-rrhh-tutor.fragment';

const MSG_CONVOCATORIAS = marker('csp.convocatoria');
const MSG_SAVE_REQUISITOS_INVESTIGADOR = marker('msg.save.solicitud.requisitos-investigador');
const MSG_PROYECTOS = marker('csp.proyecto');

export const SOLICITUD_ACTION_LINK_KEY = 'solicitud';

export interface ISolicitudData {
  readonly: boolean;
  estadoAndDocumentosReadonly: boolean;
  solicitud: ISolicitud;
  hasSolicitudProyecto: boolean;
  hasPopulatedPeriodosSocios: boolean;
  solicitudProyecto: ISolicitudProyecto;
  hasAnySolicitudProyectoSocioWithRolCoordinador: boolean;
  proyectosIds: number[];
  modificableEstadoAsTutor: boolean;
  isTutor: boolean;
  isInvestigador: boolean;
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
    AUTOEVALUACION: 'autoevaluacion',
    SOLICITANTE: 'solicitante',
    TUTOR: 'tutor',
    REQUISITOS_CONVOCATORIA: 'requisitos-convocatoria',
    MEMORIA: 'memoria'
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
  private solicitanteRrhh: SolicitudRrhhSolitanteFragment;
  private tutorRrhh: SolicitudRrhhTutorFragment;
  private requisitosConvocatoriaRrhh: SolicitudRrhhRequisitosConvocatoriaFragment;
  private memoriaRrhh: SolicitudRrhhMemoriaFragment;

  readonly showSocios$: Subject<boolean> = new BehaviorSubject(false);
  readonly showHitos$: Subject<boolean> = new BehaviorSubject<boolean>(false);
  readonly showDesglosePresupuestoGlobal$: Subject<boolean> = new BehaviorSubject<boolean>(false);
  readonly showDesglosePresupuestoEntidad$: Subject<boolean> = new BehaviorSubject<boolean>(false);
  readonly datosProyectoComplete$: Subject<boolean> = new BehaviorSubject<boolean>(false);
  readonly showAlertNotSocioCoordinadorExist$ = new BehaviorSubject<boolean>(false);
  readonly hasAnySolicitudProyectoSocioWithRolCoordinador$ = new BehaviorSubject<boolean>(false);

  private readonly data: ISolicitudData;
  private convocatoria: IConvocatoria;
  // tslint:disable-next-line: variable-name
  private _isSolicitanteInSolicitudEquipo: boolean;

  get solicitud(): ISolicitud {
    return this.datosGenerales.getValue();
  }

  get formularioSolicitud(): FormularioSolicitud {
    return this.data?.solicitud?.formularioSolicitud ?? this.datosGenerales.getValue().formularioSolicitud;
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

  get convocatoriaTitulo(): string {
    return this.convocatoria?.titulo;
  }

  get solicitante(): IPersona {
    let solicitante = this.datosGenerales.getValue().solicitante;

    if (!solicitante && this.isFormularioSolicitudRrhh() && this.solicitanteRrhh.isInitialized()) {
      solicitante = this.solicitanteRrhh.getValue().solicitante;
    }

    return solicitante;
  }

  get solicitudProyecto(): ISolicitudProyecto {
    return this.isFormularioSolicitudProyecto() ? this.proyectoDatos.getValue() : undefined;
  }

  get isSolicitanteInSolicitudEquipo(): boolean {
    return this._isSolicitanteInSolicitudEquipo;
  }

  get isAutoevaluacionEticaFullfilled() {
    return this.autoevaluacion.isFormFullFilled;
  }

  get hasRequiredDocumentos() {
    return this.documentos.hasRequiredDocumentos;
  }

  get readonly(): boolean {
    return this.data?.readonly ?? false;
  }

  get estadoAndDocumentosReadonly(): boolean {
    return this.data?.estadoAndDocumentosReadonly ?? false;
  }

  get modificableEstadoAsTutor(): boolean {
    return this.data?.modificableEstadoAsTutor ?? false;
  }

  get isTutor(): boolean {
    return this.data?.isTutor ?? false;
  }

  get isInvestigador(): boolean {
    return this.data?.isInvestigador ?? (this.isModuleINV() && this.hasAnyAuthorityInv());
  }

  constructor(
    logger: NGXLogger,
    private route: ActivatedRoute,
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
    private authService: SgiAuthService,
    solicitudProyectoAreaConocimiento: SolicitudProyectoAreaConocimientoService,
    areaConocimientoService: AreaConocimientoService,
    solicitudProyectoResponsableEconomicoService: SolicitudProyectoResponsableEconomicoService,
    formlyService: FormlyService,
    checklistService: ChecklistService,
    datosAcademicosService: DatosAcademicosService,
    vinculacionService: VinculacionService,
    convocatoriaRequisitoIpService: ConvocatoriaRequisitoIPService,
    convocatoriaRequisitoEquipoService: ConvocatoriaRequisitoEquipoService,
    private dialogService: DialogService,
    rolProyectoService: RolProyectoService,
    private translate: TranslateService,
    datosPersonalesService: DatosPersonalesService,
    palabraClaveService: PalabraClaveService,
    solicitudGrupoService: SolicitudGrupoService,
    private readonly proyectoService: ProyectoService,
    solicitudRrhhService: SolicitudRrhhService,
    solicitanteExternoService: SolicitanteExternoService,
    datosContactoService: DatosContactoService,
    nivelAcademicoService: NivelAcademicosService,
    categoriasProfesionalesService: CategoriaProfesionalService,
    solicitudRrhhRequisitoCategoriaService: SolicitudRrhhRequisitoCategoriaService,
    solicitudRrhhRequisitoNivelAcademicoService: SolicitudRrhhRequisitoNivelAcademicoService,
    documentoService: DocumentoService
  ) {
    super();

    if (route.snapshot.data[SOLICITUD_DATA_KEY]) {
      this.data = route.snapshot.data[SOLICITUD_DATA_KEY];
      this.enableEdit();
      this.datosProyectoComplete$.next(this.data.hasSolicitudProyecto);
      if (this.data.solicitud.convocatoriaId && !this.isInvestigador) {
        this.addConvocatoriaLink(this.data.solicitud.convocatoriaId);
      }
    }

    this.addProyectoLink();

    const idConvocatoria = history.state[CONVOCATORIA_ID_KEY];

    this.datosGenerales = new SolicitudDatosGeneralesFragment(
      logger,
      this.data?.solicitud?.id,
      solicitudService,
      convocatoriaService,
      empresaService,
      personaService,
      solicitudModalidadService,
      unidadGestionService,
      solicitudGrupoService,
      authService,
      this.readonly,
      this.isInvestigador
    );

    if (this.isInvestigador && idConvocatoria) {
      this.loadConvocatoria(idConvocatoria);
    }

    this.documentos = new SolicitudDocumentosFragment(
      logger,
      this.data?.solicitud?.id,
      this.data?.solicitud?.convocatoriaId,
      configuracionSolicitudService,
      solicitudService,
      solicitudDocumentoService,
      documentoService,
      this.readonly,
      this.estadoAndDocumentosReadonly
    );

    this.areaConocimiento = new SolicitudProyectoAreaConocimientoFragment(this.data?.solicitud?.id,
      solicitudProyectoAreaConocimiento, solicitudService, areaConocimientoService, this.readonly);
    this.hitos = new SolicitudHitosFragment(this.data?.solicitud?.id, solicitudHitoService, solicitudService, this.readonly);
    this.historicoEstado = new SolicitudHistoricoEstadosFragment(this.data?.solicitud?.id, solicitudService, this.readonly);
    this.proyectoDatos = new SolicitudProyectoFichaGeneralFragment(logger, this.data?.solicitud?.id,
      this.isInvestigador, solicitudService,
      solicitudProyectoService, convocatoriaService, this.readonly, this.data?.solicitud.convocatoriaId,
      this.hasAnySolicitudProyectoSocioWithRolCoordinador$, this.data?.hasPopulatedPeriodosSocios, palabraClaveService);
    this.equipoProyecto = new SolicitudEquipoProyectoFragment(
      logger,
      this.data?.solicitud?.id,
      this.data?.solicitud?.convocatoriaId,
      solicitudService,
      solicitudProyectoEquipoService,
      this,
      rolProyectoService,
      convocatoriaService,
      datosAcademicosService,
      convocatoriaRequisitoIpService,
      vinculacionService,
      convocatoriaRequisitoEquipoService,
      datosPersonalesService,
      proyectoService,
      this.isInvestigador,
      this.readonly
    );
    this.socio = new SolicitudProyectoSocioFragment(
      logger,
      this.data?.solicitud?.id,
      solicitudService,
      solicitudProyectoSocioService,
      empresaService,
      this.readonly
    );
    this.entidadesFinanciadoras = new SolicitudProyectoEntidadesFinanciadorasFragment(
      logger,
      this.data?.solicitud?.id,
      solicitudService,
      solicitudEntidadFinanciadoraService,
      empresaService,
      solicitudEntidadFinanciadoraService,
      this.readonly
    );
    this.desglosePresupuestoGlobal = new SolicitudProyectoPresupuestoGlobalFragment(this.data?.solicitud?.id, solicitudService,
      solicitudProyectoPresupuestoService, empresaService, solicitudProyectoService, this.readonly);
    this.desglosePresupuestoEntidades = new SolicitudProyectoPresupuestoEntidadesFragment(this.data?.solicitud?.id,
      this.data?.solicitud?.convocatoriaId, solicitudService, empresaService, solicitudProyectoService, solicitudEntidadFinanciadoraService,
      this.readonly);
    this.clasificaciones = new SolicitudProyectoClasificacionesFragment(this.data?.solicitud?.id, solicitudProyectoClasificacionService,
      solicitudService, clasificacionService, this.readonly);
    this.responsableEconomico = new SolicitudProyectoResponsableEconomicoFragment(
      logger,
      this.data?.solicitud?.id,
      solicitudService,
      solicitudProyectoResponsableEconomicoService,
      personaService,
      this.readonly
    );
    this.autoevaluacion = new SolicitudAutoevaluacionFragment(this.data?.solicitud, formlyService, checklistService, authService);

    // Fragments Socitudes Rrhh
    this.solicitanteRrhh = new SolicitudRrhhSolitanteFragment(
      logger,
      this.data?.solicitud,
      this.isInvestigador,
      solicitudService,
      solicitudRrhhService,
      solicitanteExternoService,
      clasificacionService,
      datosContactoService,
      datosPersonalesService,
      personaService,
      empresaService,
      this.readonly
    );

    this.tutorRrhh = new SolicitudRrhhTutorFragment(
      logger,
      this.data?.solicitud?.id,
      this.isInvestigador,
      solicitudRrhhService,
      datosContactoService,
      vinculacionService,
      personaService,
      this.readonly
    );

    this.requisitosConvocatoriaRrhh = new SolicitudRrhhRequisitosConvocatoriaFragment(
      logger,
      this.data?.solicitud?.id,
      this.data?.solicitud.convocatoriaId,
      this.data?.solicitud.estado,
      this.isInvestigador,
      convocatoriaService,
      solicitudRrhhService,
      solicitudRrhhRequisitoCategoriaService,
      solicitudRrhhRequisitoNivelAcademicoService,
      convocatoriaRequisitoEquipoService,
      convocatoriaRequisitoIpService,
      nivelAcademicoService,
      categoriasProfesionalesService,
      datosAcademicosService,
      vinculacionService,
      documentoService,
      this.readonly
    );

    this.memoriaRrhh = new SolicitudRrhhMemoriaFragment(
      logger,
      this.data?.solicitud?.id,
      this.isInvestigador,
      solicitudRrhhService,
      this.readonly
    );

    this.addFragment(this.FRAGMENT.DATOS_GENERALES, this.datosGenerales);

    this.subscriptions.push(this.datosGenerales.convocatoria$.subscribe(
      (value) => {
        this.convocatoria = value;
      }
    ));

    if (this.isEdit()) {
      this.addFragment(this.FRAGMENT.DOCUMENTOS, this.documentos);
      this.addFragment(this.FRAGMENT.HISTORICO_ESTADOS, this.historicoEstado);

      if (!this.isInvestigador) {
        this.addFragment(this.FRAGMENT.HITOS, this.hitos);


        if (this.isFormularioSolicitudProyecto()) {
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

          this.subscriptions.push(this.socio.proyectoSocios$.subscribe(
            (proyectoSocios) => {
              this.onSolicitudProyectoSocioListChangeHandle(proyectoSocios);
            }
          ));

          this.subscriptions.push(this.entidadesFinanciadoras.initialized$.subscribe(value => {
            if (value) {
              this.desglosePresupuestoEntidades.initialize();
            }
          }));
          this.subscriptions.push(this.entidadesFinanciadoras.entidadesFinanciadoras$.subscribe(entidadesFinanciadoras => {
            this.desglosePresupuestoEntidades.setEntidadesFinanciadorasEdited(entidadesFinanciadoras.map(entidad => entidad.value));
          }));

        } else if (this.isFormularioSolicitudRrhh()) {
          this.addFragment(this.FRAGMENT.SOLICITANTE, this.solicitanteRrhh);
          this.addFragment(this.FRAGMENT.TUTOR, this.tutorRrhh);
          this.addFragment(this.FRAGMENT.REQUISITOS_CONVOCATORIA, this.requisitosConvocatoriaRrhh);
          this.addFragment(this.FRAGMENT.MEMORIA, this.memoriaRrhh);
        }
      } else {
        this.subscriptions.push(this.datosGenerales.convocatoria$.subscribe(
          (value) => {
            this.convocatoria = value;
          }
        ));

        if (this.isFormularioSolicitudProyecto()) {
          this.addFragment(this.FRAGMENT.PROYECTO_DATOS, this.proyectoDatos);
          this.addFragment(this.FRAGMENT.PROYECTO_AREA_CONOCIMIENTO, this.areaConocimiento);
          this.addFragment(this.FRAGMENT.EQUIPO_PROYECTO, this.equipoProyecto);
          this.addFragment(this.FRAGMENT.CLASIFICACIONES, this.clasificaciones);
          this.addFragment(this.FRAGMENT.AUTOEVALUACION, this.autoevaluacion);
        } else if (this.isFormularioSolicitudRrhh()) {
          this.addFragment(this.FRAGMENT.SOLICITANTE, this.solicitanteRrhh);
          this.addFragment(this.FRAGMENT.TUTOR, this.tutorRrhh);
          this.addFragment(this.FRAGMENT.REQUISITOS_CONVOCATORIA, this.requisitosConvocatoriaRrhh);
          this.addFragment(this.FRAGMENT.MEMORIA, this.memoriaRrhh);
        }
      }

      // Forzamos la inicialización de los datos principales
      this.datosGenerales.initialize();

      // Inicializamos los datos del proyecto
      if (this.isFormularioSolicitudProyecto()) {
        this.proyectoDatos.initialize();
        this.datosProyectoComplete$.next(true);

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

        this.subscriptions.push(this.datosGenerales.initialized$.subscribe(
          (value) => {
            if (value && !!this.datosGenerales.getFormGroup().controls.solicitante) {
              this.equipoProyecto.initialize();
              this.documentos.initialize();
              this.subscriptions.push(this.datosGenerales.getFormGroup().controls.solicitante.valueChanges.subscribe(
                (solicitante) => {
                  if (this.equipoProyecto.proyectoEquipos$.value.length === 0 ||
                    this.equipoProyecto.proyectoEquipos$.value.filter(equipo =>
                      equipo.value.solicitudProyectoEquipo.persona?.id === solicitante?.id
                      && equipo.value.solicitudProyectoEquipo.rolProyecto?.rolPrincipal).length > 0) {
                    this.equipoProyecto.setErrors(false);
                  } else {
                    this.equipoProyecto.setErrors(true);
                  }
                }
              ));
            }
          }
        ));

        this.subscriptions.push(
          this.equipoProyecto.proyectoEquipos$.subscribe(
            (proyectoEquipo) => {
              this._isSolicitanteInSolicitudEquipo = proyectoEquipo.some(miembroEquipo =>
                miembroEquipo.value.solicitudProyectoEquipo.persona.id === this.solicitud.solicitante.id);

              if (proyectoEquipo.length === 0 ||
                proyectoEquipo.filter(equipo =>
                  equipo.value.solicitudProyectoEquipo.persona?.id === this.solicitante?.id
                  && equipo.value.solicitudProyectoEquipo.rolProyecto?.rolPrincipal).length > 0) {
                this.equipoProyecto.setErrors(false);
              } else {
                this.equipoProyecto.setErrors(true);
              }
            }
          ));

      } else if (this.isFormularioSolicitudRrhh()) {
        this.solicitanteRrhh.initialize();
        this.tutorRrhh.initialize();
        this.memoriaRrhh.initialize();

        this.subscriptions.push(
          this.solicitanteRrhh.solicitante$.subscribe(value => {
            this.requisitosConvocatoriaRrhh.solicitante$.next(value);
          })
        );

        this.subscriptions.push(
          this.requisitosConvocatoriaRrhh.initialized$.subscribe(value => {
            if (value) {
              this.requisitosConvocatoriaRrhh.solicitante$.next(this.solicitanteRrhh.solicitante$.value);
            }
          })
        );

        this.subscriptions.push(
          this.tutorRrhh.tutor$.subscribe(value => {
            this.requisitosConvocatoriaRrhh.tutor$.next(value);
          })
        );

        this.subscriptions.push(
          this.requisitosConvocatoriaRrhh.initialized$.subscribe(value => {
            if (value) {
              this.requisitosConvocatoriaRrhh.tutor$.next(this.tutorRrhh.tutor$.value);
            }
          })
        );

      }
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
    } else {
      if (!!this.convocatoriaId) {
        return this.equipoProyecto.validateRequisitosConvocatoriaSolicitante(this.solicitante, this.convocatoriaId).pipe(
          switchMap((response) => {
            if (response) {
              const errorValidacion = this.translate.instant(VALIDACION_REQUISITOS_EQUIPO_IP_MAP.get(response));
              const msgErrorValidacion = this.translate.instant(MSG_SAVE_REQUISITOS_INVESTIGADOR, { mask: errorValidacion });
              return of(msgErrorValidacion);
            }
            return this.equipoProyecto.validateRequisitosConvocatoriaGlobales(this.convocatoriaId).pipe(
              map(errorValidacion => {
                if (errorValidacion) {
                  return this.translate.instant(VALIDACION_REQUISITOS_EQUIPO_IP_MAP.get(errorValidacion));
                }

                return null;
              })
            );
          }),
          switchMap((msgErrorValidacion: string) => {
            if (msgErrorValidacion) {
              return this.dialogService.showConfirmation(msgErrorValidacion).pipe(
                switchMap((aceptado) => {
                  if (aceptado) {
                    return this.saveOrUpdateSolicitud();
                  }

                  return NEVER;
                })
              );
            }
            return this.saveOrUpdateSolicitud();
          })
        );
      }
      else {
        return this.saveOrUpdateSolicitud();
      }
    }
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

      if (this.isFormularioSolicitudRrhh()) {
        if (this.solicitanteRrhh.hasChanges()) {
          cascade = cascade.pipe(
            switchMap(() => this.solicitanteRrhh.saveOrUpdate().pipe(tap(() => this.solicitanteRrhh.refreshInitialState(true))))
          );
        }

        if (this.tutorRrhh.hasChanges()) {
          cascade = cascade.pipe(
            switchMap(() => this.tutorRrhh.saveOrUpdate().pipe(tap(() => this.tutorRrhh.refreshInitialState(true))))
          );
        }

        if (this.memoriaRrhh.hasChanges()) {
          cascade = cascade.pipe(
            switchMap(() => this.memoriaRrhh.saveOrUpdate().pipe(tap(() => this.memoriaRrhh.refreshInitialState(true))))
          );
        }
      }

      // Si se modifica alguna pestaña desde el perfil investigador y no se ha creado la ficha general se fuerza que se cree
      // aunque no tenga cambios
      if (this.isInvestigador
        && this.isFormularioSolicitudProyecto()
        && !this.proyectoDatos.isEdit()
        && (this.areaConocimiento.hasChanges()
          || this.clasificaciones.hasChanges()
          || this.equipoProyecto.hasChanges()
          || this.autoevaluacion.hasChanges())) {
        cascade = cascade.pipe(
          switchMap(() => this.proyectoDatos.saveOrUpdate().pipe(tap(() => this.proyectoDatos.refreshInitialState(true))))
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

  private addProyectoLink(): void {

    if (!this.data?.proyectosIds || this.data?.proyectosIds.length === 0) {
      return;
    }

    const proyectoId = this.data.proyectosIds.length > 1 ? null : this.data.proyectosIds[0];
    const routerLink: string[] = !!proyectoId ?
      ['../..', CSP_ROUTE_NAMES.PROYECTO, proyectoId.toString(), PROYECTO_ROUTE_NAMES.FICHA_GENERAL] :
      ['../..', CSP_ROUTE_NAMES.PROYECTO];
    const queryParams = !proyectoId ? { [SOLICITUD_ACTION_LINK_KEY]: this.data.solicitud.id } : {};
    const actionLinkOptions = {
      title: MSG_PROYECTOS,
      titleParams: MSG_PARAMS.CARDINALIRY.SINGULAR,
      routerLink,
      queryParams
    };
    if (proyectoId !== null) {
      this.subscriptions.push(
        this.proyectoService.findById(proyectoId).pipe(
          tap(proyecto => {
            if (!!!proyecto.activo) {
              return;
            }
            this.addActionLink(actionLinkOptions);
          })
        ).subscribe());
    } else {
      actionLinkOptions.titleParams = MSG_PARAMS.CARDINALIRY.PLURAL;
      this.addActionLink(actionLinkOptions);
    }
  }

  private isFormularioSolicitudProyecto(): boolean {
    return this.formularioSolicitud === FormularioSolicitud.PROYECTO;
  }

  private isFormularioSolicitudRrhh(): boolean {
    return this.formularioSolicitud === FormularioSolicitud.RRHH;
  }

  private isModuleINV(): boolean {
    return this.route.snapshot.data.module === Module.INV;
  }

  private hasAnyAuthorityInv(): boolean {
    return this.authService.hasAnyAuthority(['CSP-SOL-INV-BR', 'CSP-SOL-INV-C', 'CSP-SOL-INV-ER']);
  }

  showSolicitudRRHHToValidateInfoMessage(): boolean {
    const solicitud = this.datosGenerales.getValue();
    return solicitud.formularioSolicitud === FormularioSolicitud.RRHH && solicitud.estado.estado === Estado.BORRADOR;
  }
}
