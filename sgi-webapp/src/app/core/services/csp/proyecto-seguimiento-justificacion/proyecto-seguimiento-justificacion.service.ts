import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { IProyectoSeguimientoJustificacion } from '@core/models/csp/proyecto-seguimiento-justificacion';
import { environment } from '@env';
import { CreateCtor, mixinCreate, mixinUpdate, SgiRestBaseService, UpdateCtor } from '@sgi/framework/http';
import { IProyectoSeguimientoJustificacionRequest } from './proyecto-seguimiento-justificacion-request';
import { PROYECTO_SEGUIMIENTO_JUSTIFICACION_REQUEST_CONVERTER } from './proyecto-seguimiento-justificacion-request.converter';
import { IProyectoSeguimientoJustificacionResponse } from './proyecto-seguimiento-justificacion-response';
import { PROYECTO_SEGUIMIENTO_JUSTIFICACION_RESPONSE_CONVERTER } from './proyecto-seguimiento-justificacion-response.converter';

// tslint:disable-next-line: variable-name
const _ProyectoSeguimientoJustificacionMixinBase:
  CreateCtor<IProyectoSeguimientoJustificacion, IProyectoSeguimientoJustificacion,
    IProyectoSeguimientoJustificacionRequest, IProyectoSeguimientoJustificacionResponse> &
  UpdateCtor<number, IProyectoSeguimientoJustificacion, IProyectoSeguimientoJustificacion,
    IProyectoSeguimientoJustificacionRequest, IProyectoSeguimientoJustificacionResponse> &
  typeof SgiRestBaseService = mixinCreate(
    mixinUpdate(
      SgiRestBaseService,
      PROYECTO_SEGUIMIENTO_JUSTIFICACION_REQUEST_CONVERTER,
      PROYECTO_SEGUIMIENTO_JUSTIFICACION_RESPONSE_CONVERTER
    ),
    PROYECTO_SEGUIMIENTO_JUSTIFICACION_REQUEST_CONVERTER,
    PROYECTO_SEGUIMIENTO_JUSTIFICACION_RESPONSE_CONVERTER
  );

@Injectable({
  providedIn: 'root'
})
export class ProyectoSeguimientoJustificacionService extends _ProyectoSeguimientoJustificacionMixinBase {

  private static readonly MAPPING = '/proyectos-seguimiento-justificacion';

  constructor(protected http: HttpClient) {
    super(
      `${environment.serviceServers.csp}${ProyectoSeguimientoJustificacionService.MAPPING}`,
      http
    );
  }
}
