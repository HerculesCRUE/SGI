import { Injectable } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { FormularioSolicitud } from '@core/enums/formulario-solicitud';
import { VALIDACION_REQUISITOS_EQUIPO_IP_MAP } from '@core/enums/validaciones-requisitos-equipo-ip';
import { MSG_PARAMS } from '@core/i18n';
import { Estado } from '@core/models/csp/estado-proyecto';
import { IProyecto } from '@core/models/csp/proyecto';
import { IProyectoProyectoSge } from '@core/models/csp/proyecto-proyecto-sge';
import { IProyectoSocio } from '@core/models/csp/proyecto-socio';
import { ActionService } from '@core/services/action-service';
import { ContextoProyectoService } from '@core/services/csp/contexto-proyecto.service';
import { ConvocatoriaRequisitoEquipoService } from '@core/services/csp/convocatoria-requisito-equipo.service';
import { ConvocatoriaRequisitoIPService } from '@core/services/csp/convocatoria-requisito-ip.service';
import { ConvocatoriaService } from '@core/services/csp/convocatoria.service';
import { ModeloEjecucionService } from '@core/services/csp/modelo-ejecucion.service';
import { ProyectoAgrupacionGastoService } from '@core/services/csp/proyecto-agrupacion-gasto/proyecto-agrupacion-gasto.service';
import { ProyectoAnualidadService } from '@core/services/csp/proyecto-anualidad/proyecto-anualidad.service';
import { ProyectoAreaConocimientoService } from '@core/services/csp/proyecto-area-conocimiento.service';
import { ProyectoClasificacionService } from '@core/services/csp/proyecto-clasificacion.service';
import { ProyectoConceptoGastoService } from '@core/services/csp/proyecto-concepto-gasto.service';
import { ProyectoDocumentoService } from '@core/services/csp/proyecto-documento.service';
import { ProyectoEntidadFinanciadoraService } from '@core/services/csp/proyecto-entidad-financiadora.service';
import { ProyectoEntidadGestoraService } from '@core/services/csp/proyecto-entidad-gestora.service';
import { ProyectoEquipoService } from '@core/services/csp/proyecto-equipo.service';
import { ProyectoFacturacionService } from '@core/services/csp/proyecto-facturacion/proyecto-facturacion.service';
import { ProyectoHitoService } from '@core/services/csp/proyecto-hito/proyecto-hito.service';
import { ProyectoIVAService } from '@core/services/csp/proyecto-iva.service';
import { ProyectoPaqueteTrabajoService } from '@core/services/csp/proyecto-paquete-trabajo.service';
import { ProyectoPartidaService } from '@core/services/csp/proyecto-partida.service';
import { ProyectoPeriodoAmortizacionService } from '@core/services/csp/proyecto-periodo-amortizacion/proyecto-periodo-amortizacion.service';
import { ProyectoPeriodoJustificacionService } from '@core/services/csp/proyecto-periodo-justificacion/proyecto-periodo-justificacion.service';
import { ProyectoPeriodoSeguimientoService } from '@core/services/csp/proyecto-periodo-seguimiento.service';
import { ProyectoFaseService } from '@core/services/csp/proyecto-fase.service';
import { ProyectoProrrogaService } from '@core/services/csp/proyecto-prorroga.service';
import { ProyectoProyectoSgeService } from '@core/services/csp/proyecto-proyecto-sge.service';
import { ProyectoResponsableEconomicoService } from '@core/services/csp/proyecto-responsable-economico/proyecto-responsable-economico.service';
import { ProyectoSocioPeriodoJustificacionService } from '@core/services/csp/proyecto-socio-periodo-justificacion.service';
import { ProyectoSocioService } from '@core/services/csp/proyecto-socio.service';
import { ProyectoService } from '@core/services/csp/proyecto.service';
import { SolicitudService } from '@core/services/csp/solicitud.service';
import { TipoAmbitoGeograficoService } from '@core/services/csp/tipo-ambito-geografico.service';
import { TipoFinalidadService } from '@core/services/csp/tipo-finalidad.service';
import { UnidadGestionService } from '@core/services/csp/unidad-gestion.service';
import { DialogService } from '@core/services/dialog.service';
import { InvencionService } from '@core/services/pii/invencion/invencion.service';
import { RelacionService } from '@core/services/rel/relaciones/relacion.service';
import { DocumentoService } from '@core/services/sgdoc/documento.service';
import { FacturaPrevistaEmitidaService } from '@core/services/sge/factura-prevista-emitida/factura-prevista-emitida.service';
import { PeriodoAmortizacionService } from '@core/services/sge/periodo-amortizacion/periodo-amortizacion.service';
import { ProyectoSgeService } from '@core/services/sge/proyecto-sge.service';
import { EmpresaService } from '@core/services/sgemp/empresa.service';
import { AreaConocimientoService } from '@core/services/sgo/area-conocimiento.service';
import { ClasificacionService } from '@core/services/sgo/clasificacion.service';
import { PalabraClaveService } from '@core/services/sgo/palabra-clave.service';
import { DatosAcademicosService } from '@core/services/sgp/datos-academicos.service';
import { DatosPersonalesService } from '@core/services/sgp/datos-personales.service';
import { PersonaService } from '@core/services/sgp/persona.service';
import { VinculacionService } from '@core/services/sgp/vinculacion.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { TranslateService } from '@ngx-translate/core';
import { SgiAuthService } from '@sgi/framework/auth';
import { NGXLogger } from 'ngx-logger';
import { BehaviorSubject, merge, Observable, of, Subject, throwError } from 'rxjs';
import { map, switchMap, tap } from 'rxjs/operators';
import { CSP_ROUTE_NAMES } from '../csp-route-names';
import { PROYECTO_DATA_KEY } from './proyecto-data.resolver';
import { ProyectoAgrupacionGastoFragment } from './proyecto-formulario/proyecto-agrupaciones-gasto/proyecto-agrupaciones-gasto.fragment';
import { ProyectoAmortizacionFondosFragment } from './proyecto-formulario/proyecto-amortizacion-fondos/proyecto-amortizacion-fondos.fragment';
import { ProyectoAreaConocimientoFragment } from './proyecto-formulario/proyecto-area-conocimiento/proyecto-area-conocimiento.fragment';
import { ProyectoCalendarioFacturacionFragment } from './proyecto-formulario/proyecto-calendario-facturacion/proyecto-calendario-facturacion.fragment';
import { ProyectoCalendarioJustificacionFragment } from './proyecto-formulario/proyecto-calendario-justificacion/proyecto-calendario-justificacion.fragment';
import { ProyectoClasificacionesFragment } from './proyecto-formulario/proyecto-clasificaciones/proyecto-clasificaciones.fragment';
import { ProyectoConceptosGastoFragment } from './proyecto-formulario/proyecto-conceptos-gasto/proyecto-conceptos-gasto.fragment';
import { ProyectoConsultaPresupuestoFragment } from './proyecto-formulario/proyecto-consulta-presupuesto/proyecto-consulta-presupuesto.fragment';
import { ProyectoContextoFragment } from './proyecto-formulario/proyecto-contexto/proyecto-contexto.fragment';
import { ProyectoFichaGeneralFragment } from './proyecto-formulario/proyecto-datos-generales/proyecto-ficha-general.fragment';
import { ProyectoDocumentosFragment } from './proyecto-formulario/proyecto-documentos/proyecto-documentos.fragment';
import { ProyectoEntidadGestoraFragment } from './proyecto-formulario/proyecto-entidad-gestora/proyecto-entidad-gestora.fragment';
import { ProyectoEntidadesConvocantesFragment } from './proyecto-formulario/proyecto-entidades-convocantes/proyecto-entidades-convocantes.fragment';
import { ProyectoEntidadesFinanciadorasFragment } from './proyecto-formulario/proyecto-entidades-financiadoras/proyecto-entidades-financiadoras.fragment';
import { ProyectoEquipoFragment } from './proyecto-formulario/proyecto-equipo/proyecto-equipo.fragment';
import { ProyectoHistoricoEstadosFragment } from './proyecto-formulario/proyecto-historico-estados/proyecto-historico-estados.fragment';
import { ProyectoHitosFragment } from './proyecto-formulario/proyecto-hitos/proyecto-hitos.fragment';
import { ProyectoPaqueteTrabajoFragment } from './proyecto-formulario/proyecto-paquete-trabajo/proyecto-paquete-trabajo.fragment';
import { ProyectoPartidasPresupuestariasFragment } from './proyecto-formulario/proyecto-partidas-presupuestarias/proyecto-partidas-presupuestarias.fragment';
import { ProyectoPeriodoSeguimientosFragment } from './proyecto-formulario/proyecto-periodo-seguimientos/proyecto-periodo-seguimientos.fragment';
import { ProyectoPlazosFragment } from './proyecto-formulario/proyecto-plazos/proyecto-plazos.fragment';
import { ProyectoPresupuestoFragment } from './proyecto-formulario/proyecto-presupuesto/proyecto-presupuesto.fragment';
import { ProyectoProrrogasFragment } from './proyecto-formulario/proyecto-prorrogas/proyecto-prorrogas.fragment';
import { ProyectoProyectosSgeFragment } from './proyecto-formulario/proyecto-proyectos-sge/proyecto-proyectos-sge.fragment';
import { ProyectoRelacionFragment } from './proyecto-formulario/proyecto-relaciones/proyecto-relaciones.fragment';
import { ProyectoResponsableEconomicoFragment } from './proyecto-formulario/proyecto-responsable-economico/proyecto-responsable-economico.fragment';
import { ProyectoSociosFragment } from './proyecto-formulario/proyecto-socios/proyecto-socios.fragment';
import { PROYECTO_ROUTE_PARAMS } from './proyecto-route-params';

const MSG_SOLICITUDES = marker('csp.solicitud');
const MSG_CONVOCATORIAS = marker('csp.convocatoria');

export interface IProyectoData {
  proyecto: IProyecto;
  solicitanteRefSolicitud: string;
  solicitudFormularioSolicitud: FormularioSolicitud;
  readonly: boolean;
  disableCoordinadorExterno: boolean;
  hasAnyProyectoSocioCoordinador: boolean;
  isVisor: boolean;
  isInvestigador: boolean;
}

@Injectable()
export class ProyectoActionService extends ActionService {

  public readonly FRAGMENT = {
    FICHA_GENERAL: 'ficha-general',
    ENTIDADES_FINANCIADORAS: 'entidades-financiadoras',
    SOCIOS: 'socios',
    HITOS: 'hitos',
    ENTIDADES_CONVOCANTES: 'entidades-convocantes',
    PAQUETE_TRABAJO: 'paquete-trabajo',
    FASES: 'fases',
    CONTEXTO_PROYECTO: 'contexto-proyecto',
    AREA_CONOCIMIENTO: 'area-conocimiento',
    SEGUIMIENTO_CIENTIFICO: 'seguimiento-cientificos',
    ENTIDAD_GESTORA: 'entidad-gestora',
    EQUIPO_PROYECTO: 'equipo-proyecto',
    PRORROGAS: 'prorrogas',
    HISTORICO_ESTADOS: 'historico-estados',
    DOCUMENTOS: 'documentos',
    CLASIFICACIONES: 'clasificaciones',
    PROYECTOS_SGE: 'proyectos-sge',
    PARTIDAS_PRESUPUESTARIAS: 'partidas-presupuestarias',
    ELEGIBILIDAD: 'elegibilidad',
    PRESUPUESTO: 'presupuesto',
    REPONSABLE_ECONOMICO: 'responsable-economico',
    AGRUPACIONES_GASTO: 'agrupaciones-gasto',
    CALENDARIO_JUSTIFICACION: 'calendario-justificacion',
    CONSULTA_PRESUPUESTO: 'consulta-presupuesto',
    AMORTIZACION_FONDOS: 'amortizacion-fondos',
    RELACIONES: 'relaciones',
    CALENDARIO_FACTURACION: 'calendario-facturacion'
  };

  private fichaGeneral: ProyectoFichaGeneralFragment;
  private entidadesFinanciadoras: ProyectoEntidadesFinanciadorasFragment;
  private hitos: ProyectoHitosFragment;
  private socios: ProyectoSociosFragment;
  private entidadesConvocantes: ProyectoEntidadesConvocantesFragment;
  private paqueteTrabajo: ProyectoPaqueteTrabajoFragment;
  private plazos: ProyectoPlazosFragment;
  private proyectoContexto: ProyectoContextoFragment;
  private seguimientoCientifico: ProyectoPeriodoSeguimientosFragment;
  private entidadGestora: ProyectoEntidadGestoraFragment;
  private proyectoEquipo: ProyectoEquipoFragment;
  private documentos: ProyectoDocumentosFragment;
  private prorrogas: ProyectoProrrogasFragment;
  private historicoEstados: ProyectoHistoricoEstadosFragment;
  private clasificaciones: ProyectoClasificacionesFragment;
  private areaConocimiento: ProyectoAreaConocimientoFragment;
  private proyectosSge: ProyectoProyectosSgeFragment;
  private partidasPresupuestarias: ProyectoPartidasPresupuestariasFragment;
  private elegibilidad: ProyectoConceptosGastoFragment;
  private presupuesto: ProyectoPresupuestoFragment;
  private responsableEconomico: ProyectoResponsableEconomicoFragment;
  private proyectoAgrupacionGasto: ProyectoAgrupacionGastoFragment;
  private proyectoCalendarioJustificacion: ProyectoCalendarioJustificacionFragment;
  private consultaPresupuesto: ProyectoConsultaPresupuestoFragment;
  private amortizacionFondos: ProyectoAmortizacionFondosFragment;
  private relaciones: ProyectoRelacionFragment;
  private proyectoCalendarioFacturacion: ProyectoCalendarioFacturacionFragment;

  private readonly data: IProyectoData;

  public readonly isInvestigador: boolean;
  public readonly showPaquetesTrabajo$: Subject<boolean> = new BehaviorSubject(false);
  public readonly disableAddSocios$ = new BehaviorSubject<boolean>(false);
  private readonly hasFases$ = new BehaviorSubject<boolean>(false);
  private readonly hasHitos$ = new BehaviorSubject<boolean>(false);
  private readonly hasDocumentos$ = new BehaviorSubject<boolean>(false);
  readonly showAlertNotSocioCoordinadorExist$ = new BehaviorSubject<boolean>(false);
  readonly showSocios$: Subject<boolean> = new BehaviorSubject(false);
  public readonly proyectosSge$ = new BehaviorSubject<StatusWrapper<IProyectoProyectoSge>[]>([]);

  get proyecto(): IProyecto {
    return this.fichaGeneral.getValue();
  }

  get convocatoriaId(): number {
    return this.fichaGeneral.getValue().convocatoriaId;
  }

  get modeloEjecucionId(): number {
    return this.proyecto?.modeloEjecucion?.id;
  }

  get estado(): Estado {
    return this.data?.proyecto?.estado?.estado;
  }

  get readonly(): boolean {
    return this.data?.readonly;
  }

  get hasAnyProyectoSocioWithRolCoordinador$() {
    return this.fichaGeneral.hasAnyProyectoSocioWithRolCoordinador$;
  }

  get hasProyectoCoordinadoAndCoordinadorExterno$() {
    return this.fichaGeneral.hasProyectoCoordinadoAndCoordinadorExterno$;
  }

  get hasPopulatedSocios$() {
    return this.fichaGeneral.hasPopulatedSocios$;
  }

  get miembrosEquipoPersonaRefs(): string[] {
    return this.proyectoEquipo.equipos$.value.map(personaListado => personaListado.value.proyectoEquipo.persona.id);
  }

  get solicitanteRefSolicitud(): string {
    return this.data.solicitanteRefSolicitud ?? null;
  }

  get solicitudFormularioSolicitud(): FormularioSolicitud {
    return this.data.solicitudFormularioSolicitud ?? null;
  }

  get unidadGestionId(): number {
    return this.fichaGeneral.getValue().unidadGestion?.id;
  }

  get titulo(): string {
    return this.fichaGeneral.getValue().titulo;
  }

  constructor(
    fb: FormBuilder,
    logger: NGXLogger,
    route: ActivatedRoute,
    protected proyectoService: ProyectoService,
    empresaService: EmpresaService,
    proyectoSocioService: ProyectoSocioService,
    unidadGestionService: UnidadGestionService,
    modeloEjecucionService: ModeloEjecucionService,
    tipoFinalidadService: TipoFinalidadService,
    tipoAmbitoGeograficoService: TipoAmbitoGeograficoService,
    convocatoriaService: ConvocatoriaService,
    proyectoEntidadFinanciadoraService: ProyectoEntidadFinanciadoraService,
    proyectoHitoService: ProyectoHitoService,
    proyectoPaqueteTrabajoService: ProyectoPaqueteTrabajoService,
    proyectoPlazoService: ProyectoFaseService,
    contextoProyectoService: ContextoProyectoService,
    proyectoPeriodoSeguimientoService: ProyectoPeriodoSeguimientoService,
    documentoService: DocumentoService,
    proyectoEntidadGestora: ProyectoEntidadGestoraService,
    proyectoEquipoService: ProyectoEquipoService,
    personaService: PersonaService,
    proyectoProrrogaService: ProyectoProrrogaService,
    proyectoDocumentoService: ProyectoDocumentoService,
    solicitudService: SolicitudService,
    proyectoIvaService: ProyectoIVAService,
    proyectoSocioPeriodoJustificacionService: ProyectoSocioPeriodoJustificacionService,
    proyectoClasificacionService: ProyectoClasificacionService,
    clasificacionService: ClasificacionService,
    proyectoAreaConocimiento: ProyectoAreaConocimientoService,
    areaConocimientoService: AreaConocimientoService,
    proyectoProyectoSgeService: ProyectoProyectoSgeService,
    proyectoSgeService: ProyectoSgeService,
    proyectoPartidaService: ProyectoPartidaService,
    proyectoConceptoGastoService: ProyectoConceptoGastoService,
    proyectoResponsableEconomicoService: ProyectoResponsableEconomicoService,
    proyectoAgrupacionGastoService: ProyectoAgrupacionGastoService,
    private translate: TranslateService,
    proyectoAnualidadService: ProyectoAnualidadService,
    proyectoPeriodoJustificacionService: ProyectoPeriodoJustificacionService,
    datosAcademicosService: DatosAcademicosService,
    convocatoriaRequisitoIPService: ConvocatoriaRequisitoIPService,
    viculacionService: VinculacionService,
    convocatoriaRequisitoEquipoService: ConvocatoriaRequisitoEquipoService,
    datosPersonalesService: DatosPersonalesService,
    relacionService: RelacionService,
    invencionService: InvencionService,
    sgiAuthService: SgiAuthService,
    proyectoFacturacionService: ProyectoFacturacionService,
    facturaPrevistaEmitidaService: FacturaPrevistaEmitidaService,
    palabraClaveService: PalabraClaveService,
    proyectoPeriodoAmortizacionService: ProyectoPeriodoAmortizacionService,
    periodoAmortizacionService: PeriodoAmortizacionService,
    private dialogService: DialogService
  ) {
    super();
    this.data = route.snapshot.data[PROYECTO_DATA_KEY];
    const id = Number(route.snapshot.paramMap.get(PROYECTO_ROUTE_PARAMS.ID));

    if (this.data && id) {
      this.enableEdit();
      if (this.data.proyecto?.solicitudId && !this.data.isInvestigador) {
        this.addSolicitudLink(this.data.proyecto.solicitudId);
      }
      if (this.data.proyecto?.convocatoriaId && !this.data.isInvestigador) {
        this.addConvocatoriaLink(this.data.proyecto.convocatoriaId);
      }
    }

    this.fichaGeneral = new ProyectoFichaGeneralFragment(
      logger, fb, id, proyectoService, unidadGestionService,
      modeloEjecucionService, tipoFinalidadService, tipoAmbitoGeograficoService, convocatoriaService, solicitudService, proyectoIvaService,
      this.data?.readonly,
      this.data?.disableCoordinadorExterno,
      this.data?.hasAnyProyectoSocioCoordinador,
      this.data?.isVisor,
      this.data?.isInvestigador,
      relacionService,
      palabraClaveService,
      sgiAuthService
    );
    this.addFragment(this.FRAGMENT.FICHA_GENERAL, this.fichaGeneral);

    if (this.data?.isInvestigador) {
      this.proyectoCalendarioFacturacion = new ProyectoCalendarioFacturacionFragment(this.data?.proyecto?.id, this.data?.proyecto,
        proyectoService, proyectoFacturacionService, facturaPrevistaEmitidaService);
      this.proyectosSge = new ProyectoProyectosSgeFragment(id, proyectoProyectoSgeService, proyectoService,
        proyectoSgeService, this.readonly, this.data?.isVisor);

      this.addFragment(this.FRAGMENT.CALENDARIO_FACTURACION, this.proyectoCalendarioFacturacion);
      this.addFragment(this.FRAGMENT.PROYECTOS_SGE, this.proyectosSge);

      this.fichaGeneral.initialize();

      if (this.isEdit()) {
        this.subscriptions.push(this.fichaGeneral.initialized$.subscribe(() => this.proyectosSge.initialize()));
        this.subscriptions.push(this.proyectosSge.proyectosSge$.subscribe(value => {
          this.proyectosSge$.next(value);
        }));
      }

    } else {
      if (this.isEdit()) {
        this.entidadesFinanciadoras = new ProyectoEntidadesFinanciadorasFragment(
          id, this.data.proyecto?.solicitudId, proyectoService, proyectoEntidadFinanciadoraService, empresaService, solicitudService,
          false);
        this.socios = new ProyectoSociosFragment(id, empresaService, proyectoService, proyectoSocioService,
          this.hasAnyProyectoSocioWithRolCoordinador$, this.hasProyectoCoordinadoAndCoordinadorExterno$);
        this.hitos = new ProyectoHitosFragment(id, proyectoService, proyectoHitoService);
        this.plazos = new ProyectoPlazosFragment(id, proyectoService, proyectoPlazoService);
        this.entidadesConvocantes = new ProyectoEntidadesConvocantesFragment(logger, id, proyectoService, empresaService);
        this.paqueteTrabajo = new ProyectoPaqueteTrabajoFragment(id, proyectoService, proyectoPaqueteTrabajoService);
        this.proyectoContexto = new ProyectoContextoFragment(id, logger, contextoProyectoService, convocatoriaService,
          this.data?.proyecto?.convocatoriaId, this.readonly, this.data?.isVisor);
        this.seguimientoCientifico = new ProyectoPeriodoSeguimientosFragment(
          id, this.data.proyecto, proyectoService, proyectoPeriodoSeguimientoService, convocatoriaService, documentoService);
        this.proyectoEquipo = new ProyectoEquipoFragment(
          logger,
          id,
          this.data?.proyecto?.convocatoriaId,
          this.data?.solicitanteRefSolicitud,
          this.data?.proyecto?.estado?.estado,
          this.data.solicitudFormularioSolicitud,
          proyectoService,
          proyectoEquipoService,
          personaService,
          convocatoriaService,
          datosAcademicosService,
          convocatoriaRequisitoIPService,
          viculacionService,
          convocatoriaRequisitoEquipoService,
          datosPersonalesService
        );
        this.entidadGestora = new ProyectoEntidadGestoraFragment(
          fb, id, proyectoService, proyectoEntidadGestora, empresaService, this.readonly, this.data?.isVisor);
        this.areaConocimiento = new ProyectoAreaConocimientoFragment(this.data?.proyecto?.id,
          proyectoAreaConocimiento, proyectoService, areaConocimientoService, this.readonly, this.data?.isVisor);
        this.prorrogas = new ProyectoProrrogasFragment(id, proyectoService, proyectoProrrogaService, documentoService);
        this.historicoEstados = new ProyectoHistoricoEstadosFragment(id, proyectoService);
        this.documentos = new ProyectoDocumentosFragment(
          id, convocatoriaService, solicitudService, proyectoService, proyectoPeriodoSeguimientoService, proyectoSocioService,
          proyectoSocioPeriodoJustificacionService, proyectoProrrogaService, proyectoDocumentoService, empresaService, translate,
          this.data?.isVisor);
        this.clasificaciones = new ProyectoClasificacionesFragment(id, proyectoClasificacionService, proyectoService,
          clasificacionService, this.readonly, this.data?.isVisor);
        this.proyectosSge = new ProyectoProyectosSgeFragment(id, proyectoProyectoSgeService, proyectoService,
          proyectoSgeService, this.readonly, this.data?.isVisor);
        this.partidasPresupuestarias = new ProyectoPartidasPresupuestariasFragment(id, this.data?.proyecto,
          proyectoService, proyectoPartidaService, convocatoriaService, this.readonly);
        this.elegibilidad = new ProyectoConceptosGastoFragment(id, this.data.proyecto, proyectoService, proyectoConceptoGastoService,
          convocatoriaService, this.readonly, this.data?.isVisor);
        this.presupuesto = new ProyectoPresupuestoFragment(logger, id, proyectoService, proyectoAnualidadService,
          solicitudService, this.readonly, this.data?.isVisor);
        this.responsableEconomico = new ProyectoResponsableEconomicoFragment(id, proyectoService, proyectoResponsableEconomicoService,
          personaService, this.readonly);
        this.proyectoAgrupacionGasto = new ProyectoAgrupacionGastoFragment(this.data?.proyecto?.id, proyectoService,
          proyectoAgrupacionGastoService, this.readonly, this.data?.isVisor);
        this.proyectoCalendarioJustificacion = new ProyectoCalendarioJustificacionFragment(this.data?.proyecto?.id, this.data?.proyecto,
          proyectoService, proyectoPeriodoJustificacionService, convocatoriaService);
        this.amortizacionFondos = new ProyectoAmortizacionFondosFragment(this.data?.proyecto?.id, this.data?.proyecto?.anualidades,
          this.data.proyecto?.solicitudId, proyectoPeriodoAmortizacionService, proyectoEntidadFinanciadoraService, empresaService,
          proyectoAnualidadService, periodoAmortizacionService);
        this.consultaPresupuesto = new ProyectoConsultaPresupuestoFragment(this.data?.proyecto?.id, this.proyectoService);
        this.relaciones = new ProyectoRelacionFragment(
          id, this.data.proyecto, this.readonly, relacionService, convocatoriaService, invencionService, proyectoService, sgiAuthService);
        this.proyectoCalendarioFacturacion = new ProyectoCalendarioFacturacionFragment(this.data?.proyecto?.id, this.data?.proyecto,
          proyectoService, proyectoFacturacionService, facturaPrevistaEmitidaService);

        this.addFragment(this.FRAGMENT.ENTIDADES_FINANCIADORAS, this.entidadesFinanciadoras);
        this.addFragment(this.FRAGMENT.SOCIOS, this.socios);
        this.addFragment(this.FRAGMENT.HITOS, this.hitos);
        this.addFragment(this.FRAGMENT.FASES, this.plazos);
        this.addFragment(this.FRAGMENT.ENTIDADES_CONVOCANTES, this.entidadesConvocantes);
        this.addFragment(this.FRAGMENT.PAQUETE_TRABAJO, this.paqueteTrabajo);
        this.addFragment(this.FRAGMENT.CONTEXTO_PROYECTO, this.proyectoContexto);
        this.addFragment(this.FRAGMENT.SEGUIMIENTO_CIENTIFICO, this.seguimientoCientifico);
        this.addFragment(this.FRAGMENT.EQUIPO_PROYECTO, this.proyectoEquipo);
        this.addFragment(this.FRAGMENT.ENTIDAD_GESTORA, this.entidadGestora);
        this.addFragment(this.FRAGMENT.PRORROGAS, this.prorrogas);
        this.addFragment(this.FRAGMENT.HISTORICO_ESTADOS, this.historicoEstados);
        this.addFragment(this.FRAGMENT.DOCUMENTOS, this.documentos);
        this.addFragment(this.FRAGMENT.CLASIFICACIONES, this.clasificaciones);
        this.addFragment(this.FRAGMENT.AREA_CONOCIMIENTO, this.areaConocimiento);
        this.addFragment(this.FRAGMENT.PROYECTOS_SGE, this.proyectosSge);
        this.addFragment(this.FRAGMENT.PARTIDAS_PRESUPUESTARIAS, this.partidasPresupuestarias);
        this.addFragment(this.FRAGMENT.ELEGIBILIDAD, this.elegibilidad);
        this.addFragment(this.FRAGMENT.PRESUPUESTO, this.presupuesto);
        this.addFragment(this.FRAGMENT.REPONSABLE_ECONOMICO, this.responsableEconomico);
        this.addFragment(this.FRAGMENT.AGRUPACIONES_GASTO, this.proyectoAgrupacionGasto);
        this.addFragment(this.FRAGMENT.CALENDARIO_JUSTIFICACION, this.proyectoCalendarioJustificacion);
        this.addFragment(this.FRAGMENT.CONSULTA_PRESUPUESTO, this.consultaPresupuesto);
        this.addFragment(this.FRAGMENT.AMORTIZACION_FONDOS, this.amortizacionFondos);
        this.addFragment(this.FRAGMENT.RELACIONES, this.relaciones);
        this.addFragment(this.FRAGMENT.CALENDARIO_FACTURACION, this.proyectoCalendarioFacturacion);

        this.proyectoEquipo.initialize();

        this.subscriptions.push(this.fichaGeneral.initialized$.subscribe(value => {
          if (value) {
            this.proyectoContexto.ocultarTable = !Boolean(this.fichaGeneral.getValue()?.convocatoriaId);
          }
        }));
        this.subscriptions.push(this.fichaGeneral.colaborativo$.subscribe((value) => {
          this.disableAddSocios$.next(!Boolean(value));
        }));
        this.subscriptions.push(this.fichaGeneral.permitePaquetesTrabajo$.subscribe((value) => {
          this.showPaquetesTrabajo$.next(Boolean(value));
        }));

        // Sincronización de las entidades financiadoras
        this.subscriptions.push(this.amortizacionFondos.initialized$.subscribe(value => {
          if (value) {
            this.entidadesFinanciadoras.initialize();
          }
        }));
        this.subscriptions.push(this.entidadesFinanciadoras.entidadesFinanciadorasSincronizadas$.subscribe(entidadesFinanciadoras => {
          this.amortizacionFondos.entidadesFinanciadoras$.next(entidadesFinanciadoras);
        }));

        // Sincronización periodosAmortizacion
        this.subscriptions.push(this.presupuesto.initialized$.subscribe(value => {
          if (value) {
            this.amortizacionFondos.initialize();
          }
        }));
        this.subscriptions.push(this.amortizacionFondos.periodosAmortizacion$.subscribe(periodosAmortizacion => {
          this.presupuesto.anualidadesWithPeriodoAmortizacion$
            .next(periodosAmortizacion.map(periodoAmortizacion => periodoAmortizacion.value.proyectoAnualidad.id));
        }));

        // Sincronización de las vinculaciones sobre modelo de ejecución
        if (this.isEdit() && !this.readonly && !this.data?.isInvestigador) {
          // Checks on init
          this.subscriptions.push(
            proyectoService.hasProyectoFases(id).subscribe(value => this.hasFases$.next(value))
          );
          this.subscriptions.push(
            proyectoService.hasProyectoHitos(id).subscribe(value => this.hasHitos$.next(value))
          );
          this.subscriptions.push(
            proyectoService.hasProyectoDocumentos(id).subscribe(value => this.hasDocumentos$.next(value))
          );
          this.subscriptions.push(
            this.prorrogas.ultimaProrroga$.subscribe(
              (value) => this.fichaGeneral.ultimaProrroga$.next(value)
            )
          );

          // Propagate changes
          this.subscriptions.push(
            merge(
              this.hasFases$,
              this.hasHitos$,
              this.hasDocumentos$
            ).subscribe(
              () => {
                this.fichaGeneral.vinculacionesModeloEjecucion$.next(
                  this.hasFases$.value
                  || this.hasHitos$.value
                  || this.hasDocumentos$.value
                );
              }
            )
          );

          this.subscriptions.push(this.proyectosSge$.subscribe(value => {
            this.fichaGeneral.vinculacionesProyectosSge$.next(value.length > 0);
            this.amortizacionFondos.proyectosSGE$.next(value.map(wraper => wraper.value));
          }));

          // Syncronize changes
          this.subscriptions.push(this.plazos.plazos$.subscribe(value => this.hasFases$.next(!!value.length)));
          this.subscriptions.push(this.hitos.hitos$.subscribe(value => this.hasHitos$.next(!!value.length)));
          this.subscriptions.push(this.documentos.documentos$.subscribe(value => this.hasDocumentos$.next(!!value.length)));
          this.subscriptions.push(this.fichaGeneral.coordinado$.subscribe(
            (value: boolean) => {
              this.showSocios$.next(value);
            }
          ));
        }


        this.subscriptions.push(
          this.socios.proyectoSocios$.subscribe(proyectoSocios => this.onProyectoSocioListChangeHandle(proyectoSocios))
        );

        // Inicializamos la ficha general de forma predeterminada
        this.fichaGeneral.initialize();
        if (this.isEdit()) {
          this.subscriptions.push(this.fichaGeneral.initialized$.subscribe(() => this.proyectosSge.initialize()));
          this.subscriptions.push(this.proyectosSge.proyectosSge$.subscribe(value => {
            this.proyectosSge$.next(value);
          }));
        }

        this.subscriptions.push(
          this.fichaGeneral.initialized$.subscribe(value => {
            if (value) {
              this.proyectoEquipo.initialize();
            }
          }));

        this.subscribeToMiembrosProyectoEquipoChangeList();

      }
    }

    this.subscriptions.push(this.fichaGeneral.iva$.subscribe(newIVA => {
      if (this.proyectoCalendarioFacturacion) {
        this.proyectoCalendarioFacturacion.proyectoIVA = newIVA;
      }
    }));
  }

  private subscribeToMiembrosProyectoEquipoChangeList(): void {
    if (this.proyectoEquipo) {
      this.subscriptions.push(
        this.proyectoEquipo.equipos$.subscribe(personas =>
          this.relaciones.miembrosEquipoProyecto = personas.map(personaListado => personaListado.value.proyectoEquipo.persona))
      );
    }
  }

  private addSolicitudLink(idSolicitud: number): void {
    this.addActionLink({
      title: MSG_SOLICITUDES,
      titleParams: MSG_PARAMS.CARDINALIRY.SINGULAR,
      routerLink: ['../..', CSP_ROUTE_NAMES.SOLICITUD, idSolicitud.toString()]
    });
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

    if (!this.isEdit() || !!!this.convocatoriaId || this.data.isInvestigador) {
      return this.saveOrUpdateProyecto();
    }

    return this.proyectoEquipo.validateRequisitosConvocatoriaGlobales(this.convocatoriaId).pipe(
      map(errorValidacion => {
        if (errorValidacion) {
          return this.translate.instant(VALIDACION_REQUISITOS_EQUIPO_IP_MAP.get(errorValidacion));
        }

        return null;
      }),
      switchMap((msgErrorValidacion: string) => {
        if (msgErrorValidacion) {
          return this.dialogService.showConfirmation(msgErrorValidacion).pipe(
            switchMap((aceptado) => {
              if (aceptado) {
                return this.saveOrUpdateProyecto();
              }
            })
          );
        }
        return this.saveOrUpdateProyecto();
      })
    );
  }

  private saveOrUpdateProyecto(): Observable<void> {
    let cascade = of(void 0);

    if (this.isEdit()) {
      if (this.prorrogas?.hasChanges()) {
        cascade = cascade.pipe(
          switchMap(() => this.prorrogas.saveOrUpdate().pipe(tap(() => this.prorrogas.refreshInitialState(true))))
        );
      }
      if (this.proyectosSge?.hasChanges()) {
        cascade = cascade.pipe(
          switchMap(() => this.proyectosSge.saveOrUpdate().pipe(tap(() => this.proyectosSge.refreshInitialState(true))))
        );
      }
    } else {
      if (this.fichaGeneral?.hasChanges()) {
        cascade = cascade.pipe(
          switchMap(() => this.fichaGeneral.saveOrUpdate().pipe(
            tap((key) => {
              this.fichaGeneral.refreshInitialState(true);
              if (typeof key === 'string' || typeof key === 'number') {
                this.onKeyChange(key);
              }
            })
          )
          )
        );
      }
    }

    return cascade.pipe(
      switchMap(() => super.saveOrUpdate())
    );
  }

  private onProyectoSocioListChangeHandle(proyectoSocios: StatusWrapper<IProyectoSocio>[]): void {

    if (this.data?.proyecto?.coordinado) {
      const numSocios = proyectoSocios.length;
      this.hasPopulatedSocios$.next(numSocios > 0);
    }
    let needShow = false;
    if (this.data?.proyecto?.coordinado && this.data?.proyecto?.coordinadorExterno) {
      const socioCoordinador = proyectoSocios.find((socio: StatusWrapper<IProyectoSocio>) => socio.value.rolSocio.coordinador);

      if (socioCoordinador) {
        needShow = false;
      } else {
        needShow = true;
      }
    } else {
      needShow = false;
    }
    this.hasAnyProyectoSocioWithRolCoordinador$.next(!needShow);
  }
}
