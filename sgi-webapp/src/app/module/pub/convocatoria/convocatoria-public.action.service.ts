import { Injectable, OnDestroy } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { ActionService } from '@core/services/action-service';
import { ConvocatoriaPublicService } from '@core/services/csp/convocatoria-public.service';
import { ConvocatoriaRequisitoEquipoPublicService } from '@core/services/csp/convocatoria-requisito-equipo-public.service';
import { ConvocatoriaRequisitoIPPublicService } from '@core/services/csp/convocatoria-requisito-ip-public.service';
import { UnidadGestionPublicService } from '@core/services/csp/unidad-gestion-public.service';
import { PartidaPresupuestariaGastoSgePublicService } from '@core/services/sge/partida-presupuestaria-sge/partida-presupuestaria-gasto-sge-public.service';
import { PartidaPresupuestariaIngresoSgePublicService } from '@core/services/sge/partida-presupuestaria-sge/partida-presupuestaria-ingreso-sge-public.service';
import { EmpresaPublicService } from '@core/services/sgemp/empresa-public.service';
import { CategoriaProfesionalPublicService } from '@core/services/sgp/categoria-profesional-public.service';
import { NivelAcademicoPublicService } from '@core/services/sgp/nivel-academico-public.service';
import { NGXLogger } from 'ngx-logger';
import { Observable } from 'rxjs';
import { CONVOCATORIA_ROUTE_PARAMS } from '../../csp/convocatoria/convocatoria-route-params';
import { ConvocatoriaConceptoGastoPublicFragment } from './convocatoria-formulario/convocatoria-concepto-gasto-public/convocatoria-concepto-public-gasto.fragment';
import { ConvocatoriaDatosGeneralesPublicFragment } from './convocatoria-formulario/convocatoria-datos-generales-public/convocatoria-datos-generales-public.fragment';
import { ConvocatoriaDocumentosPublicFragment } from './convocatoria-formulario/convocatoria-documentos-public/convocatoria-documentos-public.fragment';
import { ConvocatoriaEnlacePublicFragment } from './convocatoria-formulario/convocatoria-enlace-public/convocatoria-enlace-public.fragment';
import { ConvocatoriaEntidadesConvocantesPublicFragment } from './convocatoria-formulario/convocatoria-entidades-convocantes-public/convocatoria-entidades-convocantes-public.fragment';
import { ConvocatoriaEntidadesFinanciadorasPublicFragment } from './convocatoria-formulario/convocatoria-entidades-financiadoras-public/convocatoria-entidades-financiadoras-public.fragment';
import { ConvocatoriaHitosPublicFragment } from './convocatoria-formulario/convocatoria-hitos-public/convocatoria-hitos-public.fragment';
import { ConvocatoriaPartidaPresupuestariaPublicFragment } from './convocatoria-formulario/convocatoria-partidas-presupuestarias-public/convocatoria-partidas-presupuestarias-public.fragment';
import { ConvocatoriaPeriodosJustificacionPublicFragment } from './convocatoria-formulario/convocatoria-periodos-justificacion-public/convocatoria-periodos-justificacion-public.fragment';
import { ConvocatoriaPlazosFasesPublicFragment } from './convocatoria-formulario/convocatoria-plazos-fases-public/convocatoria-plazos-fases-public.fragment';
import { ConvocatoriaRequisitosEquipoPublicFragment } from './convocatoria-formulario/convocatoria-requisitos-equipo-public/convocatoria-requisitos-equipo-public.fragment';
import { ConvocatoriaRequisitosIPPublicFragment } from './convocatoria-formulario/convocatoria-requisitos-ip-public/convocatoria-requisitos-ip-public.fragment';
import { ConvocatoriaSeguimientoCientificoPublicFragment } from './convocatoria-formulario/convocatoria-seguimiento-cientifico-public/convocatoria-seguimiento-cientifico-public.fragment';

export const CONVOCATORIA_ACTION_LINK_KEY = 'convocatoria';

@Injectable()
export class ConvocatoriaPublicActionService extends ActionService implements OnDestroy {

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
    PARTIDAS_PRESUPUESTARIAS: 'partidas-presupuestarias'
  };

  private datosGenerales: ConvocatoriaDatosGeneralesPublicFragment;
  private plazosFases: ConvocatoriaPlazosFasesPublicFragment;
  private periodoJustificacion: ConvocatoriaPeriodosJustificacionPublicFragment;
  private seguimientoCientifico: ConvocatoriaSeguimientoCientificoPublicFragment;
  private hitos: ConvocatoriaHitosPublicFragment;
  private entidadesConvocantes: ConvocatoriaEntidadesConvocantesPublicFragment;
  private entidadesFinanciadoras: ConvocatoriaEntidadesFinanciadorasPublicFragment;
  private enlaces: ConvocatoriaEnlacePublicFragment;
  private requisitosIP: ConvocatoriaRequisitosIPPublicFragment;
  private elegibilidad: ConvocatoriaConceptoGastoPublicFragment;
  private requisitosEquipo: ConvocatoriaRequisitosEquipoPublicFragment;
  private documentos: ConvocatoriaDocumentosPublicFragment;
  private partidasPresupuestarias: ConvocatoriaPartidaPresupuestariaPublicFragment;

  public readonly id: number;

  constructor(
    fb: FormBuilder,
    logger: NGXLogger,
    route: ActivatedRoute,
    categoriaProfesionaService: CategoriaProfesionalPublicService,
    convocatoriaRequisitoEquipoService: ConvocatoriaRequisitoEquipoPublicService,
    convocatoriaRequisitoIPService: ConvocatoriaRequisitoIPPublicService,
    convocatoriaService: ConvocatoriaPublicService,
    empresaService: EmpresaPublicService,
    nivelAcademicoService: NivelAcademicoPublicService,
    partidaPresupuestariaGastoSgeService: PartidaPresupuestariaGastoSgePublicService,
    partidaPresupuestariaIngresoSgeService: PartidaPresupuestariaIngresoSgePublicService,
    unidadGestionService: UnidadGestionPublicService
  ) {
    super();
    this.id = Number(route.snapshot.paramMap.get(CONVOCATORIA_ROUTE_PARAMS.ID));
    if (this.id) {
      this.enableEdit();
    }

    this.datosGenerales = new ConvocatoriaDatosGeneralesPublicFragment(
      logger,
      this.id,
      convocatoriaService,
      empresaService,
      unidadGestionService
    );
    this.periodoJustificacion = new ConvocatoriaPeriodosJustificacionPublicFragment(
      this.id,
      convocatoriaService
    );
    this.entidadesConvocantes = new ConvocatoriaEntidadesConvocantesPublicFragment(
      logger,
      this.id,
      convocatoriaService,
      empresaService
    );
    this.plazosFases = new ConvocatoriaPlazosFasesPublicFragment(
      this.id,
      convocatoriaService
    );
    this.hitos = new ConvocatoriaHitosPublicFragment(
      this.id,
      convocatoriaService
    );
    this.documentos = new ConvocatoriaDocumentosPublicFragment(
      logger,
      this.id,
      convocatoriaService
    );
    this.seguimientoCientifico = new ConvocatoriaSeguimientoCientificoPublicFragment(
      this.id,
      convocatoriaService
    );
    this.entidadesFinanciadoras = new ConvocatoriaEntidadesFinanciadorasPublicFragment(
      this.id,
      convocatoriaService
    );
    this.enlaces = new ConvocatoriaEnlacePublicFragment(
      this.id,
      convocatoriaService
    );
    this.requisitosIP = new ConvocatoriaRequisitosIPPublicFragment(
      fb,
      this.id,
      convocatoriaRequisitoIPService,
      nivelAcademicoService,
      categoriaProfesionaService
    );
    this.requisitosEquipo = new ConvocatoriaRequisitosEquipoPublicFragment(
      fb,
      this.id,
      convocatoriaRequisitoEquipoService,
      nivelAcademicoService,
      categoriaProfesionaService
    );
    this.partidasPresupuestarias = new ConvocatoriaPartidaPresupuestariaPublicFragment(
      this.id,
      convocatoriaService,
      partidaPresupuestariaGastoSgeService,
      partidaPresupuestariaIngresoSgeService
    );
    this.elegibilidad = new ConvocatoriaConceptoGastoPublicFragment(
      this.id,
      convocatoriaService
    );

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

    // Inicializamos los datos generales
    this.datosGenerales.initialize();
  }

  saveOrUpdate(): Observable<void> {
    throw new Error('Method not implemented.');
  }

  /**
   * Acción de registro de una convocatoria
   */
  registrar(): Observable<void> {
    throw new Error('Method not implemented.');
  }
}
