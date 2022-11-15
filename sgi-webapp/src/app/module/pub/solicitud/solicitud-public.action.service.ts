import { Injectable } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { FormularioSolicitud } from '@core/enums/formulario-solicitud';
import { IConvocatoria } from '@core/models/csp/convocatoria';
import { Estado, IEstadoSolicitud } from '@core/models/csp/estado-solicitud';
import { ISolicitud } from '@core/models/csp/solicitud';
import { ISolicitudProyecto } from '@core/models/csp/solicitud-proyecto';
import { ActionService } from '@core/services/action-service';
import { ConfiguracionSolicitudPublicService } from '@core/services/csp/configuracion-solicitud-public.service';
import { ConvocatoriaPublicService } from '@core/services/csp/convocatoria-public.service';
import { ConvocatoriaRequisitoEquipoPublicService } from '@core/services/csp/convocatoria-requisito-equipo-public.service';
import { ConvocatoriaRequisitoIPPublicService } from '@core/services/csp/convocatoria-requisito-ip-public.service';
import { SolicitanteExternoPublicService } from '@core/services/csp/solicitante-externo/solicitante-externo-public.service';
import { SolicitudDocumentoPublicService } from '@core/services/csp/solicitud-documento-public.service';
import { SolicitudModalidadPublicService } from '@core/services/csp/solicitud-modalidad-public.service';
import { SolicitudPublicService } from '@core/services/csp/solicitud-public.service';
import { SolicitudRrhhRequisitoCategoriaPublicService } from '@core/services/csp/solicitud-rrhh-requisito-categoria/solicitud-rrhh-requisito-categoria-public.service';
import { SolicitudRrhhRequisitoNivelAcademicoPublicService } from '@core/services/csp/solicitud-rrhh-requisito-nivel-academico/solicitud-rrhh-requisito-nivel-academico-public.service';
import { SolicitudRrhhPublicService } from '@core/services/csp/solicitud-rrhh/solicitud-rrhh-public.service';
import { UnidadGestionPublicService } from '@core/services/csp/unidad-gestion-public.service';
import { DocumentoPublicService } from '@core/services/sgdoc/documento-public.service';
import { EmpresaPublicService } from '@core/services/sgemp/empresa-public.service';
import { ClasificacionPublicService } from '@core/services/sgo/clasificacion-public.service';
import { CategoriaProfesionalPublicService } from '@core/services/sgp/categoria-profesional-public.service';
import { DatosAcademicosPublicService } from '@core/services/sgp/datos-academicos-public.service';
import { DatosContactoPublicService } from '@core/services/sgp/datos-contacto/datos-contacto-public.service';
import { NivelAcademicoPublicService } from '@core/services/sgp/nivel-academico-public.service';
import { PersonaPublicService } from '@core/services/sgp/persona-public.service';
import { VinculacionPublicService } from '@core/services/sgp/vinculacion/vinculacion-public.service';
import { NGXLogger } from 'ngx-logger';
import { Observable, of, throwError } from 'rxjs';
import { switchMap, tap } from 'rxjs/operators';
import { CONVOCATORIA_PUBLIC_ID_KEY } from './solicitud-crear/solicitud-public-crear.guard';
import { SolicitudDatosGeneralesPublicFragment } from './solicitud-formulario/solicitud-datos-generales-public/solicitud-datos-generales-public.fragment';
import { SolicitudDocumentosPublicFragment } from './solicitud-formulario/solicitud-documentos-public/solicitud-documentos-public.fragment';
import { SolicitudHistoricoEstadosPublicFragment } from './solicitud-formulario/solicitud-historico-estados-public/solicitud-historico-estados-public.fragment';
import { SolicitudRrhhMemoriaPublicFragment } from './solicitud-formulario/solicitud-rrhh-memoria-public/solicitud-rrhh-memoria-public.fragment';
import { SolicitudRrhhRequisitosConvocatoriaPublicFragment } from './solicitud-formulario/solicitud-rrhh-requisitos-convocatoria-public/solicitud-rrhh-requisitos-convocatoria-public.fragment';
import { SolicitudRrhhTutorPublicFragment } from './solicitud-formulario/solicitud-rrhh-tutor-public/solicitud-rrhh-tutor-public.fragment';
import { SOLICITUD_PUBLIC_DATA_KEY } from './solicitud-public-data.resolver';

const MSG_CONVOCATORIAS = marker('csp.convocatoria');

export const SOLICITUD_ACTION_LINK_KEY = 'solicitud';

export interface ISolicitudPublicData {
  readonly: boolean;
  estadoAndDocumentosReadonly: boolean;
  solicitud: ISolicitud;
  publicKey: string;
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
export class SolicitudPublicActionService extends ActionService {

  public readonly FRAGMENT = {
    DATOS_GENERALES: 'datosGenerales',
    HISTORICO_ESTADOS: 'historicoEstados',
    DOCUMENTOS: 'documentos',
    TUTOR: 'tutor',
    REQUISITOS_CONVOCATORIA: 'requisitos-convocatoria',
    MEMORIA: 'memoria'
  };

  private datosGenerales: SolicitudDatosGeneralesPublicFragment;
  private historicoEstado: SolicitudHistoricoEstadosPublicFragment;
  private documentos: SolicitudDocumentosPublicFragment;
  private tutorRrhh: SolicitudRrhhTutorPublicFragment;
  private requisitosConvocatoriaRrhh: SolicitudRrhhRequisitosConvocatoriaPublicFragment;
  private memoriaRrhh: SolicitudRrhhMemoriaPublicFragment;


  private readonly data: ISolicitudPublicData;
  private convocatoria: IConvocatoria;

  get solicitud(): ISolicitud {
    return this.datosGenerales.getValue();
  }

  get formularioSolicitud(): FormularioSolicitud {
    return this.data?.solicitud?.formularioSolicitud ?? this.datosGenerales.getValue().formularioSolicitud;
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

  get publicId(): string {
    return this.data?.publicKey;
  }

  constructor(
    logger: NGXLogger,
    private route: ActivatedRoute,
    private solicitudService: SolicitudPublicService,
    configuracionSolicitudService: ConfiguracionSolicitudPublicService,
    private convocatoriaService: ConvocatoriaPublicService,
    empresaService: EmpresaPublicService,
    personaService: PersonaPublicService,
    solicitudModalidadService: SolicitudModalidadPublicService,
    unidadGestionService: UnidadGestionPublicService,
    solicitudDocumentoService: SolicitudDocumentoPublicService,
    clasificacionService: ClasificacionPublicService,
    datosAcademicosService: DatosAcademicosPublicService,
    vinculacionService: VinculacionPublicService,
    convocatoriaRequisitoIpService: ConvocatoriaRequisitoIPPublicService,
    convocatoriaRequisitoEquipoService: ConvocatoriaRequisitoEquipoPublicService,
    solicitudRrhhService: SolicitudRrhhPublicService,
    solicitanteExternoService: SolicitanteExternoPublicService,
    datosContactoService: DatosContactoPublicService,
    nivelAcademicoService: NivelAcademicoPublicService,
    categoriasProfesionalesService: CategoriaProfesionalPublicService,
    solicitudRrhhRequisitoCategoriaService: SolicitudRrhhRequisitoCategoriaPublicService,
    solicitudRrhhRequisitoNivelAcademicoService: SolicitudRrhhRequisitoNivelAcademicoPublicService,
    documentoService: DocumentoPublicService
  ) {
    super();

    if (route.snapshot.data[SOLICITUD_PUBLIC_DATA_KEY]) {
      this.data = route.snapshot.data[SOLICITUD_PUBLIC_DATA_KEY];
      this.enableEdit();
    }

    const idConvocatoria = history.state[CONVOCATORIA_PUBLIC_ID_KEY];

    this.datosGenerales = new SolicitudDatosGeneralesPublicFragment(
      logger,
      this.data?.publicKey,
      solicitudService,
      convocatoriaService,
      empresaService,
      solicitudModalidadService,
      unidadGestionService,
      clasificacionService,
      solicitudRrhhService,
      solicitanteExternoService,
      this.readonly
    );

    if (idConvocatoria) {
      this.loadConvocatoria(idConvocatoria);
    }

    this.documentos = new SolicitudDocumentosPublicFragment(
      logger,
      this.data?.solicitud?.id,
      this.data?.publicKey,
      this.data?.solicitud?.convocatoriaId,
      configuracionSolicitudService,
      solicitudService,
      solicitudDocumentoService,
      documentoService,
      this.readonly,
      this.estadoAndDocumentosReadonly
    );

    this.historicoEstado = new SolicitudHistoricoEstadosPublicFragment(
      this.data?.publicKey,
      solicitudService,
      this.readonly
    );

    // Fragments Socitudes Rrhh
    this.tutorRrhh = new SolicitudRrhhTutorPublicFragment(
      logger,
      this.data?.publicKey,
      true,
      solicitudRrhhService,
      datosContactoService,
      vinculacionService,
      personaService,
      this.readonly
    );

    this.requisitosConvocatoriaRrhh = new SolicitudRrhhRequisitosConvocatoriaPublicFragment(
      this.data?.solicitud?.id,
      this.data?.publicKey,
      this.data?.solicitud.convocatoriaId,
      this.data?.solicitud.estado,
      true,
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

    this.memoriaRrhh = new SolicitudRrhhMemoriaPublicFragment(
      logger,
      this.data?.publicKey,
      true,
      solicitudRrhhService,
      this.readonly
    );

    this.addFragment(this.FRAGMENT.DATOS_GENERALES, this.datosGenerales);


    if (this.isEdit()) {
      this.addFragment(this.FRAGMENT.DOCUMENTOS, this.documentos);
      this.addFragment(this.FRAGMENT.HISTORICO_ESTADOS, this.historicoEstado);
      this.addFragment(this.FRAGMENT.TUTOR, this.tutorRrhh);
      this.addFragment(this.FRAGMENT.REQUISITOS_CONVOCATORIA, this.requisitosConvocatoriaRrhh);
      this.addFragment(this.FRAGMENT.MEMORIA, this.memoriaRrhh);


      this.subscriptions.push(this.datosGenerales.convocatoria$.subscribe(
        (value) => {
          this.convocatoria = value;
        }
      ));

      // Forzamos la inicialización de los datos principales
      this.datosGenerales.initialize();

      this.tutorRrhh.initialize();
      this.memoriaRrhh.initialize();

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

  /**
   * Cambio de estado a **Presentada** desde:
   * - **Borrador**
   */
  cambiarEstado(estadoNuevo: IEstadoSolicitud): Observable<IEstadoSolicitud> {
    return this.solicitudService.cambiarEstado(this.publicId, estadoNuevo);
  }



  private loadConvocatoria(id: number): void {
    if (id) {
      this.convocatoriaService.findById(id).subscribe(convocatoria => {
        this.datosGenerales.setDatosConvocatoria(convocatoria);
      });
    }
  }

  saveOrUpdate(): Observable<void> {
    this.performChecks(true);
    if (this.hasErrors()) {
      return throwError('Errores');
    }

    let cascade = of(void 0);

    if (this.isEdit()) {
      if (this.datosGenerales.hasChanges()) {
        cascade = cascade.pipe(
          switchMap(() => this.datosGenerales.saveOrUpdate().pipe(tap(() => this.datosGenerales.refreshInitialState(true))))
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

    return cascade.pipe(
      switchMap(() => super.saveOrUpdate())
    );
  }

  showSolicitudRRHHToValidateInfoMessage(): boolean {
    const solicitud = this.datosGenerales.getValue();
    return solicitud.formularioSolicitud === FormularioSolicitud.RRHH && solicitud.estado.estado === Estado.BORRADOR;
  }
}
