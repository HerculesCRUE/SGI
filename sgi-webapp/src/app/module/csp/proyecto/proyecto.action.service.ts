import { Injectable } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { MSG_PARAMS } from '@core/i18n';
import { Estado, IEstadoProyecto } from '@core/models/csp/estado-proyecto';
import { IProyecto } from '@core/models/csp/proyecto';
import { IProyectoProyectoSge } from '@core/models/csp/proyecto-proyecto-sge';
import { IProyectoSocio } from '@core/models/csp/proyecto-socio';
import { ActionService } from '@core/services/action-service';
import { ContextoProyectoService } from '@core/services/csp/contexto-proyecto.service';
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
import { ProyectoHitoService } from '@core/services/csp/proyecto-hito.service';
import { ProyectoIVAService } from '@core/services/csp/proyecto-iva.service';
import { ProyectoPaqueteTrabajoService } from '@core/services/csp/proyecto-paquete-trabajo.service';
import { ProyectoPartidaService } from '@core/services/csp/proyecto-partida.service';
import { ProyectoPeriodoJustificacionService } from '@core/services/csp/proyecto-periodo-justificacion/proyecto-periodo-justificacion.service';
import { ProyectoPeriodoSeguimientoService } from '@core/services/csp/proyecto-periodo-seguimiento.service';
import { ProyectoPlazoService } from '@core/services/csp/proyecto-plazo.service';
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
import { DocumentoService } from '@core/services/sgdoc/documento.service';
import { ProyectoSgeService } from '@core/services/sge/proyecto-sge.service';
import { EmpresaService } from '@core/services/sgemp/empresa.service';
import { AreaConocimientoService } from '@core/services/sgo/area-conocimiento.service';
import { ClasificacionService } from '@core/services/sgo/clasificacion.service';
import { PersonaService } from '@core/services/sgp/persona.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { TranslateService } from '@ngx-translate/core';
import { SgiAuthService } from '@sgi/framework/auth';
import { NGXLogger } from 'ngx-logger';
import { BehaviorSubject, merge, Observable, of, Subject, throwError } from 'rxjs';
import { switchMap, tap } from 'rxjs/operators';
import { CSP_ROUTE_NAMES } from '../csp-route-names';
import { PROYECTO_DATA_KEY } from './proyecto-data.resolver';
import { ProyectoAgrupacionGastoFragment } from './proyecto-formulario/proyecto-agrupaciones-gasto/proyecto-agrupaciones-gasto.fragment';
import { ProyectoAreaConocimientoFragment } from './proyecto-formulario/proyecto-area-conocimiento/proyecto-area-conocimiento.fragment';
import { ProyectoCalendarioJustificacionFragment } from './proyecto-formulario/proyecto-calendario-justificacion/proyecto-calendario-justificacion.fragment';
import { ProyectoClasificacionesFragment } from './proyecto-formulario/proyecto-clasificaciones/proyecto-clasificaciones.fragment';
import { ProyectoConceptosGastoFragment } from './proyecto-formulario/proyecto-conceptos-gasto/proyecto-conceptos-gasto.fragment';
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
import { ProyectoResponsableEconomicoFragment } from './proyecto-formulario/proyecto-responsable-economico/proyecto-responsable-economico.fragment';
import { ProyectoSociosFragment } from './proyecto-formulario/proyecto-socios/proyecto-socios.fragment';
import { PROYECTO_ROUTE_PARAMS } from './proyecto-route-params';

const MSG_SOLICITUDES = marker('csp.solicitud');
const MSG_CONVOCATORIAS = marker('csp.convocatoria');

export interface IProyectoData {
  proyecto: IProyecto;
  readonly: boolean;
  disableCoordinadorExterno: boolean;
  hasAnyProyectoSocioCoordinador: boolean;
  isVisor: boolean;
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
    CALENDARIO_JUSTIFICACION: 'calendario-justificacion'
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

  private readonly data: IProyectoData;

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

  get modeloEjecucionId(): number {
    return this.proyecto?.modeloEjecucion?.id;
  }

  get estado(): Estado {
    return this.fichaGeneral.getValue().estado?.estado;
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
    proyectoPlazoService: ProyectoPlazoService,
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
    translate: TranslateService,
    proyectoAnualidadService: ProyectoAnualidadService,
    proyectoPeriodoJustificacionService: ProyectoPeriodoJustificacionService
  ) {
    super();
    this.data = route.snapshot.data[PROYECTO_DATA_KEY];
    const id = Number(route.snapshot.paramMap.get(PROYECTO_ROUTE_PARAMS.ID));

    if (this.data && id) {
      this.enableEdit();
      if (this.data.proyecto?.solicitudId) {
        this.addSolicitudLink(this.data.proyecto.solicitudId);
      }
      if (this.data.proyecto?.convocatoriaId) {
        this.addConvocatoriaLink(this.data.proyecto.convocatoriaId);
      }
    }

    this.fichaGeneral = new ProyectoFichaGeneralFragment(
      logger, fb, id, proyectoService, unidadGestionService,
      modeloEjecucionService, tipoFinalidadService, tipoAmbitoGeograficoService, convocatoriaService, solicitudService, proyectoIvaService,
      this.data?.readonly,
      this.data?.disableCoordinadorExterno,
      this.data?.hasAnyProyectoSocioCoordinador,
      this.data?.isVisor
    );

    this.addFragment(this.FRAGMENT.FICHA_GENERAL, this.fichaGeneral);
    if (this.isEdit()) {
      this.entidadesFinanciadoras = new ProyectoEntidadesFinanciadorasFragment(
        id, this.data.proyecto?.solicitudId, proyectoService, proyectoEntidadFinanciadoraService, empresaService, solicitudService, false);
      this.socios = new ProyectoSociosFragment(id, empresaService, proyectoService, proyectoSocioService,
        this.hasAnyProyectoSocioWithRolCoordinador$, this.hasProyectoCoordinadoAndCoordinadorExterno$);
      this.hitos = new ProyectoHitosFragment(id, proyectoService, proyectoHitoService);
      this.plazos = new ProyectoPlazosFragment(id, proyectoService, proyectoPlazoService);
      this.entidadesConvocantes = new ProyectoEntidadesConvocantesFragment(logger, id, proyectoService, empresaService);
      this.paqueteTrabajo = new ProyectoPaqueteTrabajoFragment(id, proyectoService, proyectoPaqueteTrabajoService);
      this.proyectoContexto = new ProyectoContextoFragment(id, logger, contextoProyectoService, this.readonly, this.data?.isVisor);
      this.seguimientoCientifico = new ProyectoPeriodoSeguimientosFragment(
        id, this.data.proyecto, proyectoService, proyectoPeriodoSeguimientoService, convocatoriaService, documentoService);
      this.proyectoEquipo = new ProyectoEquipoFragment(logger, id, proyectoService, proyectoEquipoService, personaService);
      this.entidadGestora = new ProyectoEntidadGestoraFragment(
        fb, id, proyectoService, proyectoEntidadGestora, empresaService, this.readonly, this.data?.isVisor);
      this.areaConocimiento = new ProyectoAreaConocimientoFragment(this.data?.proyecto?.id,
        proyectoAreaConocimiento, proyectoService, areaConocimientoService, this.readonly, this.data?.isVisor);
      this.prorrogas = new ProyectoProrrogasFragment(id, proyectoService, proyectoProrrogaService, documentoService);
      this.historicoEstados = new ProyectoHistoricoEstadosFragment(id, proyectoService);
      this.documentos = new ProyectoDocumentosFragment(
        id, convocatoriaService, solicitudService, proyectoService, proyectoPeriodoSeguimientoService, proyectoSocioService,
        proyectoSocioPeriodoJustificacionService, proyectoProrrogaService, proyectoDocumentoService, empresaService, translate);
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

      // Sincronización de las vinculaciones sobre modelo de ejecución
      if (this.isEdit() && !this.readonly) {
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

        this.subscriptions.push(this.proyectosSge$.subscribe(value =>
          this.fichaGeneral.vinculacionesProyectosSge$.next(value.length > 0)));

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
    if (this.isEdit()) {
      let cascade = of(void 0);
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
      return cascade.pipe(
        switchMap(() => super.saveOrUpdate())
      );
    } else {
      return super.saveOrUpdate();
    }
  }

  /**
   * Cambio de estado a **Presentada** desde:
   * - **Borrador**
   */
  cambiarEstado(estadoNuevo: IEstadoProyecto): Observable<void> {
    return this.proyectoService.cambiarEstado(this.fichaGeneral.getKey() as number, estadoNuevo);
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