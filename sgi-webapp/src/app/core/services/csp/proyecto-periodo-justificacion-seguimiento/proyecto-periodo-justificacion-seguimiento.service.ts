import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { IProyectoPeriodoJustificacionSeguimiento } from '@core/models/csp/proyecto-periodo-justificacion-seguimiento';
import { environment } from '@env';
import { CreateCtor, FindByIdCtor, mixinCreate, mixinFindById, mixinUpdate, SgiRestBaseService, UpdateCtor } from '@sgi/framework/http';
import { IProyectoPeriodoJustificacionSeguimientoRequest } from './proyecto-periodo-justificacion-seguimiento-request';
import { PROYECTO_PERIODO_JUSTIFICACION_SEGUIMIENTO_REQUEST_CONVERTER } from './proyecto-periodo-justificacion-seguimiento-request.converter';
import { IProyectoPeriodoJustificacionSeguimientoResponse } from './proyecto-periodo-justificacion-seguimiento-response';
import { PROYECTO_PERIODO_JUSTIFICACION_SEGUIMIENTO_RESPONSE_CONVERTER } from './proyecto-periodo-justificacion-seguimiento-response.converter';

// tslint:disable-next-line: variable-name
const _ProyectoPeriodoJustificacionSeguimientoMixinBase:
  FindByIdCtor<number, IProyectoPeriodoJustificacionSeguimiento, IProyectoPeriodoJustificacionSeguimientoResponse> &
  CreateCtor<IProyectoPeriodoJustificacionSeguimiento, IProyectoPeriodoJustificacionSeguimiento,
    IProyectoPeriodoJustificacionSeguimientoRequest, IProyectoPeriodoJustificacionSeguimientoResponse> &
  UpdateCtor<number, IProyectoPeriodoJustificacionSeguimiento, IProyectoPeriodoJustificacionSeguimiento,
    IProyectoPeriodoJustificacionSeguimientoRequest, IProyectoPeriodoJustificacionSeguimientoResponse> &
  typeof SgiRestBaseService = mixinFindById(
    mixinCreate(
      mixinUpdate(
        SgiRestBaseService,
        PROYECTO_PERIODO_JUSTIFICACION_SEGUIMIENTO_REQUEST_CONVERTER,
        PROYECTO_PERIODO_JUSTIFICACION_SEGUIMIENTO_RESPONSE_CONVERTER
      ),
      PROYECTO_PERIODO_JUSTIFICACION_SEGUIMIENTO_REQUEST_CONVERTER,
      PROYECTO_PERIODO_JUSTIFICACION_SEGUIMIENTO_RESPONSE_CONVERTER
    ),
    PROYECTO_PERIODO_JUSTIFICACION_SEGUIMIENTO_RESPONSE_CONVERTER
  );

@Injectable({
  providedIn: 'root'
})
export class ProyectoPeriodoJustificacionSeguimientoService extends _ProyectoPeriodoJustificacionSeguimientoMixinBase {

  private static readonly MAPPING = '/proyectos-periodos-justificacion-seguimiento';

  constructor(protected http: HttpClient) {
    super(
      `${environment.serviceServers.csp}${ProyectoPeriodoJustificacionSeguimientoService.MAPPING}`,
      http
    );
  }
}
