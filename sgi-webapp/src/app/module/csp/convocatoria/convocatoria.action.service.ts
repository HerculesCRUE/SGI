import { Injectable, OnDestroy } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { MSG_PARAMS } from '@core/i18n';
import { Estado } from '@core/models/csp/convocatoria';
import { IConvocatoriaFase } from '@core/models/csp/convocatoria-fase';
import { IModeloEjecucion } from '@core/models/csp/tipos-configuracion';
import { ActionService } from '@core/services/action-service';
import { ConfigService } from '@core/services/csp/configuracion/config.service';
import { ConfiguracionSolicitudService } from '@core/services/csp/configuracion-solicitud.service';
import { ConvocatoriaAreaTematicaService } from '@core/services/csp/convocatoria-area-tematica.service';
import { ConvocatoriaConceptoGastoService } from '@core/services/csp/convocatoria-concepto-gasto.service';
import { ConvocatoriaDocumentoService } from '@core/services/csp/convocatoria-documento.service';
import { ConvocatoriaEntidadFinanciadoraService } from '@core/services/csp/convocatoria-entidad-financiadora.service';
import { ConvocatoriaEntidadGestoraService } from '@core/services/csp/convocatoria-entidad-gestora.service';
import { ConvocatoriaFaseService } from '@core/services/csp/convocatoria-fase/convocatoria-fase.service';
import { ConvocatoriaHitoService } from '@core/services/csp/convocatoria-hito/convocatoria-hito.service';
import { ConvocatoriaPartidaPresupuestariaService } from '@core/services/csp/convocatoria-partida-presupuestaria/convocatoria-partida-presupuestaria.service';
import { ConvocatoriaPeriodoJustificacionService } from '@core/services/csp/convocatoria-periodo-justificacion.service';
import { ConvocatoriaRequisitoEquipoService } from '@core/services/csp/convocatoria-requisito-equipo.service';
import { ConvocatoriaRequisitoIPService } from '@core/services/csp/convocatoria-requisito-ip.service';
import { ConvocatoriaSeguimientoCientificoService } from '@core/services/csp/convocatoria-seguimiento-cientifico.service';
import { ConvocatoriaService } from '@core/services/csp/convocatoria.service';
import { DocumentoRequeridoSolicitudService } from '@core/services/csp/documento-requerido-solicitud.service';
import { ProyectoService } from '@core/services/csp/proyecto.service';
import { UnidadGestionService } from '@core/services/csp/unidad-gestion.service';
import { DialogService } from '@core/services/dialog.service';
import { DocumentoService } from '@core/services/sgdoc/documento.service';
import { PartidaPresupuestariaGastoSgeService } from '@core/services/sge/partida-presupuestaria-sge/partida-presupuestaria-gasto-sge.service';
import { PartidaPresupuestariaIngresoSgeService } from '@core/services/sge/partida-presupuestaria-sge/partida-presupuestaria-ingreso-sge.service';
import { EmpresaService } from '@core/services/sgemp/empresa.service';
import { PalabraClaveService } from '@core/services/sgo/palabra-clave.service';
import { CategoriaProfesionalService } from '@core/services/sgp/categoria-profesional.service';
import { NivelAcademicosService } from '@core/services/sgp/nivel-academico.service';
import { NGXLogger } from 'ngx-logger';
import { BehaviorSubject, Observable, Subject, merge, of, throwError } from 'rxjs';
import { switchMap, tap } from 'rxjs/operators';
import { CSP_ROUTE_NAMES } from '../csp-route-names';
import { CONVOCATORIA_DATA_KEY } from './convocatoria-data.resolver';
import { ConvocatoriaConceptoGastoFragment } from './convocatoria-formulario/convocatoria-concepto-gasto/convocatoria-concepto-gasto.fragment';
import { ConvocatoriaConfiguracionSolicitudesFragment } from './convocatoria-formulario/convocatoria-configuracion-solicitudes/convocatoria-configuracion-solicitudes.fragment';
import { ConvocatoriaDatosGeneralesFragment } from './convocatoria-formulario/convocatoria-datos-generales/convocatoria-datos-generales.fragment';
import { ConvocatoriaDocumentosFragment } from './convocatoria-formulario/convocatoria-documentos/convocatoria-documentos.fragment';
import { ConvocatoriaEnlaceFragment } from './convocatoria-formulario/convocatoria-enlace/convocatoria-enlace.fragment';
import { ConvocatoriaEntidadesConvocantesFragment } from './convocatoria-formulario/convocatoria-entidades-convocantes/convocatoria-entidades-convocantes.fragment';
import { ConvocatoriaEntidadesFinanciadorasFragment } from './convocatoria-formulario/convocatoria-entidades-financiadoras/convocatoria-entidades-financiadoras.fragment';
import { ConvocatoriaHitosFragment } from './convocatoria-formulario/convocatoria-hitos/convocatoria-hitos.fragment';
import { ConvocatoriaPartidaPresupuestariaFragment } from './convocatoria-formulario/convocatoria-partidas-presupuestarias/convocatoria-partidas-presupuestarias.fragment';
import { ConvocatoriaPeriodosJustificacionFragment } from './convocatoria-formulario/convocatoria-periodos-justificacion/convocatoria-periodos-justificacion.fragment';
import { ConvocatoriaPlazosFasesFragment } from './convocatoria-formulario/convocatoria-plazos-fases/convocatoria-plazos-fases.fragment';
import { ConvocatoriaRequisitosEquipoFragment } from './convocatoria-formulario/convocatoria-requisitos-equipo/convocatoria-requisitos-equipo.fragment';
import { ConvocatoriaRequisitosIPFragment } from './convocatoria-formulario/convocatoria-requisitos-ip/convocatoria-requisitos-ip.fragment';
import { ConvocatoriaSeguimientoCientificoFragment } from './convocatoria-formulario/convocatoria-seguimiento-cientifico/convocatoria-seguimiento-cientifico.fragment';
import { CONVOCATORIA_ROUTE_PARAMS } from './convocatoria-route-params';

const MSG_REGISTRAR = marker('msg.csp.convocatoria.registrar');
const MSG_SOLICITUDES = marker('csp.solicitud');
const MSG_PROYECTOS = marker('csp.proyecto');

export const CONVOCATORIA_ACTION_LINK_KEY = 'convocatoria';

export interface IConvocatoriaData {
  readonly: boolean;
  canEdit: boolean;
  showSolicitudesLink: boolean;
  showProyectosLink: boolean;
  estado: Estado;
}

@Injectable()
export class ConvocatoriaActionService extends ActionService implements OnDestroy {

  public readonly FRAGMENT = {
    DATOS_GENERALES: 'datos-generales',
    PERIODO_JUSTIFICACION: 'periodos-justificacion',
    FASES: 'fases',
    HITOS: 'hitos',
    ENTIDADES_CONVOCANTES: 'entidades-convocantes',
    SEGUIMIENTO_CIENTIFICO: 'seguimiento-cientifico',
    ENLACES: 'enlaces',
    ENTIDADES_FINANCIADORAS: 'entidades-financiadoras',
    REQUISITOS_IP: 'requisitos-ip',
    ELEGIBILIDAD: 'elegibilidad',
    REQUISITOS_EQUIPO: 'requisitos-equipo',
    DOCUMENTOS: 'documentos',
    PARTIDAS_PRESUPUESTARIAS: 'partidas-presupuestarias',
    CONFIGURACION_SOLICITUDES: 'configuracion-solicitudes'
  };

  private datosGenerales: ConvocatoriaDatosGeneralesFragment;
  private plazosFases: ConvocatoriaPlazosFasesFragment;
  private periodoJustificacion: ConvocatoriaPeriodosJustificacionFragment;
  private seguimientoCientifico: ConvocatoriaSeguimientoCientificoFragment;
  private hitos: ConvocatoriaHitosFragment;
  private entidadesConvocantes: ConvocatoriaEntidadesConvocantesFragment;
  private entidadesFinanciadoras: ConvocatoriaEntidadesFinanciadorasFragment;
  private enlaces: ConvocatoriaEnlaceFragment;
  private requisitosIP: ConvocatoriaRequisitosIPFragment;
  private elegibilidad: ConvocatoriaConceptoGastoFragment;
  private requisitosEquipo: ConvocatoriaRequisitosEquipoFragment;
  private documentos: ConvocatoriaDocumentosFragment;
  private configuracionSolicitudes: ConvocatoriaConfiguracionSolicitudesFragment;
  private partidasPresupuestarias: ConvocatoriaPartidaPresupuestariaFragment;

  private dialogService: DialogService;

  private readonly data: IConvocatoriaData;
  public readonly id: number;

  public readonly hasModeloEjecucion$: Subject<boolean> = new BehaviorSubject<boolean>(false);
  private readonly hasEnlaces$ = new BehaviorSubject<boolean>(false);
  private readonly hasPlazos$ = new BehaviorSubject<boolean>(false);
  private readonly hasHitos$ = new BehaviorSubject<boolean>(false);
  private readonly hasDocumentos$ = new BehaviorSubject<boolean>(false);

  private modeloEjecucion: IModeloEjecucion;
  get modeloEjecucionId(): number {
    return this.modeloEjecucion?.id;
  }

  get duracion(): number {
    return this.datosGenerales.getValue().duracion;
  }

  get readonly(): boolean {
    return this.data?.readonly ?? false;
  }

  get canEdit(): boolean {
    return this.data?.canEdit ?? true;
  }

  get unidadGestionId(): number {
    return this.datosGenerales.getValue().unidadGestion?.id;
  }

  get titulo(): string {
    return this.datosGenerales.getValue().titulo;
  }

  constructor(
    fb: FormBuilder,
    logger: NGXLogger,
    route: ActivatedRoute,
    dialogService: DialogService,
    private categoriaProfesionaService: CategoriaProfesionalService,
    private configService: ConfigService,
    private configuracionSolicitudService: ConfiguracionSolicitudService,
    private convocatoriaAreaTematicaService: ConvocatoriaAreaTematicaService,
    private convocatoriaConceptoGastoService: ConvocatoriaConceptoGastoService,
    private convocatoriaDocumentoService: ConvocatoriaDocumentoService,
    private convocatoriaEntidadFinanciadoraService: ConvocatoriaEntidadFinanciadoraService,
    private convocatoriaEntidadGestoraService: ConvocatoriaEntidadGestoraService,
    private convocatoriaFaseService: ConvocatoriaFaseService,
    private convocatoriaHitoService: ConvocatoriaHitoService,
    private convocatoriaPartidaPresupuestariaService: ConvocatoriaPartidaPresupuestariaService,
    private convocatoriaPeriodoJustificacionService: ConvocatoriaPeriodoJustificacionService,
    private convocatoriaRequisitoEquipoService: ConvocatoriaRequisitoEquipoService,
    private convocatoriaRequisitoIPService: ConvocatoriaRequisitoIPService,
    private convocatoriaSeguimientoCientificoService: ConvocatoriaSeguimientoCientificoService,
    private convocatoriaService: ConvocatoriaService,
    private documentoRequeridoSolicitudService: DocumentoRequeridoSolicitudService,
    private documentoService: DocumentoService,
    private empresaService: EmpresaService,
    private nivelAcademicoService: NivelAcademicosService,
    private palabraClaveService: PalabraClaveService,
    private partidaPresupuestariaGastoSgeService: PartidaPresupuestariaGastoSgeService,
    private partidaPresupuestariaIngresoSgeService: PartidaPresupuestariaIngresoSgeService,
    private proyectoService: ProyectoService,
    private unidadGestionService: UnidadGestionService
  ) {
    super();
    this.id = Number(route.snapshot.paramMap.get(CONVOCATORIA_ROUTE_PARAMS.ID));
    this.dialogService = dialogService;
    if (this.id) {
      this.data = route.snapshot.data[CONVOCATORIA_DATA_KEY];
      this.enableEdit();
      this.addSolicitudLink(this.id);
      this.addProyectoLink(this.id);
    }

    this.datosGenerales = new ConvocatoriaDatosGeneralesFragment(
      logger, this.id, convocatoriaService, proyectoService, empresaService,
      convocatoriaEntidadGestoraService, unidadGestionService, convocatoriaAreaTematicaService, configuracionSolicitudService,
      this.readonly, this.canEdit, palabraClaveService);
    this.periodoJustificacion = new ConvocatoriaPeriodosJustificacionFragment(
      this.id, convocatoriaService, convocatoriaPeriodoJustificacionService, this.canEdit);
    this.entidadesConvocantes = new ConvocatoriaEntidadesConvocantesFragment(
      logger, this.id, convocatoriaService,
      empresaService, this.readonly, this.canEdit);
    this.plazosFases = new ConvocatoriaPlazosFasesFragment(
      this.id, convocatoriaService, convocatoriaFaseService, this.readonly, this.canEdit);
    this.hitos = new ConvocatoriaHitosFragment(this.id, convocatoriaService,
      convocatoriaHitoService, this.readonly, this.canEdit);
    this.documentos = new ConvocatoriaDocumentosFragment(
      logger,
      this.id,
      convocatoriaService,
      convocatoriaDocumentoService,
      documentoService,
      this.readonly,
      this.canEdit
    );
    this.seguimientoCientifico = new ConvocatoriaSeguimientoCientificoFragment(this.id,
      convocatoriaService, convocatoriaSeguimientoCientificoService, this.canEdit);
    this.entidadesFinanciadoras = new ConvocatoriaEntidadesFinanciadorasFragment(
      this.id, convocatoriaService, convocatoriaEntidadFinanciadoraService, this.readonly, this.canEdit);
    this.enlaces = new ConvocatoriaEnlaceFragment(this.id, convocatoriaService, this.readonly, this.canEdit);
    this.requisitosIP = new ConvocatoriaRequisitosIPFragment(fb, this.id,
      convocatoriaRequisitoIPService, nivelAcademicoService, categoriaProfesionaService, this.canEdit);
    this.requisitosEquipo = new ConvocatoriaRequisitosEquipoFragment(fb, this.id,
      convocatoriaRequisitoEquipoService, nivelAcademicoService, categoriaProfesionaService, this.canEdit);
    this.configuracionSolicitudes = new ConvocatoriaConfiguracionSolicitudesFragment(
      logger, this.id, configuracionSolicitudService, documentoRequeridoSolicitudService,
      this.readonly, this.canEdit);
    this.partidasPresupuestarias = new ConvocatoriaPartidaPresupuestariaFragment(
      this.id,
      configService,
      convocatoriaPartidaPresupuestariaService,
      convocatoriaService,
      partidaPresupuestariaGastoSgeService,
      partidaPresupuestariaIngresoSgeService,
      this.readonly,
      this.canEdit,
      this.data?.estado
    );
    this.elegibilidad = new ConvocatoriaConceptoGastoFragment(this.id, convocatoriaService,
      convocatoriaConceptoGastoService, this.readonly, this.canEdit);

    this.addFragment(this.FRAGMENT.DATOS_GENERALES, this.datosGenerales);
    this.addFragment(this.FRAGMENT.SEGUIMIENTO_CIENTIFICO, this.seguimientoCientifico);
    this.addFragment(this.FRAGMENT.ENTIDADES_CONVOCANTES, this.entidadesConvocantes);
    this.addFragment(this.FRAGMENT.ENTIDADES_FINANCIADORAS, this.entidadesFinanciadoras);
    this.addFragment(this.FRAGMENT.PERIODO_JUSTIFICACION, this.periodoJustificacion);
    this.addFragment(this.FRAGMENT.FASES, this.plazosFases);
    this.addFragment(this.FRAGMENT.HITOS, this.hitos);
    this.addFragment(this.FRAGMENT.PARTIDAS_PRESUPUESTARIAS, this.partidasPresupuestarias);
    this.addFragment(this.FRAGMENT.DOCUMENTOS, this.documentos);
    this.addFragment(this.FRAGMENT.ENLACES, this.enlaces);
    this.addFragment(this.FRAGMENT.REQUISITOS_IP, this.requisitosIP);
    this.addFragment(this.FRAGMENT.ELEGIBILIDAD, this.elegibilidad);
    this.addFragment(this.FRAGMENT.REQUISITOS_EQUIPO, this.requisitosEquipo);
    this.addFragment(this.FRAGMENT.CONFIGURACION_SOLICITUDES, this.configuracionSolicitudes);

    this.subscriptions.push(this.configuracionSolicitudes.initialized$.subscribe(value => {
      if (value) {
        this.plazosFases.initialize();
      }
    }));
    this.subscriptions.push(this.plazosFases.initialized$.subscribe(value => {
      if (value) {
        this.configuracionSolicitudes.initialize();
      }
    }));
    this.subscriptions.push(this.plazosFases.plazosFase$.subscribe(fases => {
      this.configuracionSolicitudes.setFases(fases.map(fase => fase.value));
    }));
    this.subscriptions.push(this.datosGenerales.modeloEjecucion$.subscribe(
      (modeloEjecucion) => {
        this.modeloEjecucion = modeloEjecucion;
        this.hasModeloEjecucion$.next(Boolean(modeloEjecucion));
      }
    ));

    this.subscriptions.push(this.configuracionSolicitudes.fasePresentacionSolicitudes$.subscribe(fase => {
      this.plazosFases.setFasePresentacionSolicitudes(fase);
    }));

    // Sincronización de las vinculaciones sobre modelo de ejecución
    if (this.isEdit() && !this.readonly) {
      // Checks on init
      this.subscriptions.push(
        this.convocatoriaService.hasConvocatoriaEnlaces(this.id).subscribe(value => this.hasEnlaces$.next(value))
      );
      this.subscriptions.push(
        this.convocatoriaService.hasConvocatoriaFases(this.id).subscribe(value => this.hasPlazos$.next(value))
      );
      this.subscriptions.push(
        this.convocatoriaService.hasConvocatoriaHitos(this.id).subscribe(value => this.hasHitos$.next(value))
      );
      this.subscriptions.push(
        this.convocatoriaService.hasConvocatoriaDocumentos(this.id).subscribe(value => this.hasDocumentos$.next(value))
      );

      // Propagate changes
      this.subscriptions.push(
        merge(
          this.hasEnlaces$,
          this.hasPlazos$,
          this.hasHitos$,
          this.hasDocumentos$
        ).subscribe(
          () => {
            this.datosGenerales.vinculacionesModeloEjecucion$.next(
              this.hasEnlaces$.value
              || this.hasPlazos$.value
              || this.hasHitos$.value
              || this.hasDocumentos$.value
            );
          }
        )
      );

      // Syncronize changes
      this.subscriptions.push(this.enlaces.enlace$.subscribe(value => this.hasEnlaces$.next(!!value.length)));
      this.subscriptions.push(this.plazosFases.plazosFase$.subscribe(value => this.hasPlazos$.next(!!value.length)));
      this.subscriptions.push(this.hitos.hitos$.subscribe(value => this.hasHitos$.next(!!value.length)));
      this.subscriptions.push(this.documentos.documentos$.subscribe(value => this.hasDocumentos$.next(!!value.length)));
    }

    // Inicializamos los datos generales
    this.datosGenerales.initialize();
  }

  private addSolicitudLink(convocatoriaId: number): void {
    if (Estado.BORRADOR === this.data.estado || !this.data.showSolicitudesLink) {
      return;
    }
    this.addActionLink({
      title: MSG_SOLICITUDES,
      titleParams: MSG_PARAMS.CARDINALIRY.PLURAL,
      routerLink: ['../..', CSP_ROUTE_NAMES.SOLICITUD],
      queryParams: { [CONVOCATORIA_ACTION_LINK_KEY]: convocatoriaId }
    });
  }

  private addProyectoLink(convocatoriaId: number): void {
    if (Estado.BORRADOR === this.data.estado || !this.data.showProyectosLink) {
      return;
    }
    this.addActionLink({
      title: MSG_PROYECTOS,
      titleParams: MSG_PARAMS.CARDINALIRY.PLURAL,
      routerLink: ['../..', CSP_ROUTE_NAMES.PROYECTO],
      queryParams: { [CONVOCATORIA_ACTION_LINK_KEY]: convocatoriaId }
    });
  }
  /**
   * Cuando se elimina una fase se actualizan los datos de la pestaña configuración solicitudes.
   */
  isDelete(convocatoriaFaseEliminada: IConvocatoriaFase): boolean {
    const fasePresentacionSolicitudes = this.configuracionSolicitudes.getValue()?.fasePresentacionSolicitudes;

    if (!fasePresentacionSolicitudes) {
      return true;
    }

    return !(convocatoriaFaseEliminada.tipoFase.id === fasePresentacionSolicitudes?.tipoFase?.id
      && convocatoriaFaseEliminada.fechaInicio.toMillis() === fasePresentacionSolicitudes?.fechaInicio?.toMillis()
      && convocatoriaFaseEliminada.fechaFin.toMillis() === fasePresentacionSolicitudes?.fechaFin?.toMillis()
      && convocatoriaFaseEliminada.observaciones === fasePresentacionSolicitudes?.observaciones);
  }

  saveOrUpdate(): Observable<void> {
    this.performChecks(true);
    if (this.hasErrors()) {
      return throwError('Errores');
    }
    if (this.isEdit()) {
      let cascade = of(void 0);
      if (this.datosGenerales.hasChanges()) {
        cascade = cascade.pipe(
          switchMap(() => this.datosGenerales.saveOrUpdate().pipe(tap(() => this.datosGenerales.refreshInitialState(true))))
        );
      }
      if (this.plazosFases.hasChanges()) {
        cascade = cascade.pipe(
          switchMap(() => this.plazosFases.saveOrUpdate().pipe(tap(() => this.plazosFases.refreshInitialState(true))))
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
      if (this.plazosFases.hasChanges()) {
        cascade = cascade.pipe(
          switchMap(() => this.plazosFases.saveOrUpdate().pipe(tap(() => this.plazosFases.refreshInitialState(true))))
        );
      }
      return cascade.pipe(
        switchMap(() => super.saveOrUpdate())
      );
    }
  }

  /**
   * Acción de registro de una convocatoria
   */
  registrar(): Observable<void> {
    return this.dialogService.showConfirmation(MSG_REGISTRAR).pipe(
      switchMap((accept) => {
        if (accept) {
          return this.convocatoriaService.registrar(this.id);
        }
      })
    );
  }
}
