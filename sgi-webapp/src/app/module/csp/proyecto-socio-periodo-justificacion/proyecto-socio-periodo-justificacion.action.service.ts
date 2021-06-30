import { Injectable } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { IProyecto } from '@core/models/csp/proyecto';
import { IProyectoSocio } from '@core/models/csp/proyecto-socio';
import { IProyectoSocioPeriodoJustificacion } from '@core/models/csp/proyecto-socio-periodo-justificacion';
import { ActionService } from '@core/services/action-service';
import { ProyectoSocioPeriodoJustificacionDocumentoService } from '@core/services/csp/proyecto-socio-periodo-justificacion-documento.service';
import { ProyectoSocioPeriodoJustificacionService } from '@core/services/csp/proyecto-socio-periodo-justificacion.service';
import { NGXLogger } from 'ngx-logger';
import { PROYECTO_SOCIO_PERIODO_JUSTIFICACION_DATA_KEY } from './proyecto-socio-periodo-justificacion-data.resolver';
import { ProyectoSocioPeriodoJustificacionDatosGeneralesFragment } from './proyecto-socio-periodo-justificacion-formulario/proyecto-socio-periodo-justificacion-datos-generales/proyecto-socio-periodo-justificacion-datos-generales.fragment';
import { ProyectoSocioPeriodoJustificacionDocumentosFragment } from './proyecto-socio-periodo-justificacion-formulario/proyecto-socio-periodo-justificacion-documentos/proyecto-socio-periodo-justificacion-documentos.fragment';
import { PROYECTO_SOCIO_PERIODO_JUSTIFICACION_ROUTE_PARAMS } from './proyecto-socio-periodo-justificacion-route-params';

export interface IProyectoSocioPeriodoJustificacionData {
  proyecto: IProyecto;
  proyectoSocio: IProyectoSocio;
  proyectoSocioPeriodosJustificacion: IProyectoSocioPeriodoJustificacion[];
}

@Injectable()
export class ProyectoSocioPeriodoJustificacionActionService extends ActionService {

  public readonly FRAGMENT = {
    DATOS_GENERALES: 'datos-generales',
    DOCUMENTOS: 'documentos'
  };

  private datosGenerales: ProyectoSocioPeriodoJustificacionDatosGeneralesFragment;
  private documentos: ProyectoSocioPeriodoJustificacionDocumentosFragment;

  private data: IProyectoSocioPeriodoJustificacionData;

  constructor(
    logger: NGXLogger,
    route: ActivatedRoute,
    proyectoSocioPeriodoJustificacionService: ProyectoSocioPeriodoJustificacionService,
    proyectoSocioPeriodoJustificacionDocumentoService: ProyectoSocioPeriodoJustificacionDocumentoService
  ) {
    super();

    this.data = route.snapshot.data[PROYECTO_SOCIO_PERIODO_JUSTIFICACION_DATA_KEY];
    const id = Number(route.snapshot.paramMap.get(PROYECTO_SOCIO_PERIODO_JUSTIFICACION_ROUTE_PARAMS.ID));

    if (id) {
      this.enableEdit();
    }

    this.datosGenerales = new ProyectoSocioPeriodoJustificacionDatosGeneralesFragment(
      id, proyectoSocioPeriodoJustificacionService, this.data.proyectoSocio,
      this.data.proyecto.estado, this.data?.proyectoSocioPeriodosJustificacion);
    this.documentos = new ProyectoSocioPeriodoJustificacionDocumentosFragment(logger, id,
      proyectoSocioPeriodoJustificacionService, proyectoSocioPeriodoJustificacionDocumentoService);

    this.addFragment(this.FRAGMENT.DATOS_GENERALES, this.datosGenerales);
    this.addFragment(this.FRAGMENT.DOCUMENTOS, this.documentos);
  }
}
