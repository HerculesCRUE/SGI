import { Estado, ISolicitudProyectoSge } from '@core/models/sge/solicitud-proyecto-sge';
import { SgiBaseConverter } from '@sgi/framework/core';
import { ISolicitudProyectoSgeResponse } from './solicitud-proyecto-sge-response';

class SolicitudProyectoSgeConverter extends SgiBaseConverter<ISolicitudProyectoSgeResponse, ISolicitudProyectoSge> {
  toTarget(value: ISolicitudProyectoSgeResponse): ISolicitudProyectoSge {
    if (!value) {
      return value as unknown as ISolicitudProyectoSge;
    }
    return {
      id: value.id,
      proyectoSgeRef: value.proyectoSgeRef,
      estado: Object.values(Estado).find(estado => estado === value.estado)
    };
  }
  fromTarget(value: ISolicitudProyectoSge): ISolicitudProyectoSgeResponse {
    if (!value) {
      return value as unknown as ISolicitudProyectoSgeResponse;
    }
    return {
      id: value.id,
      proyectoSgeRef: value.proyectoSgeRef,
      estado: value.estado
    };
  }
}

export const SOLICITUD_PROYECTO_SGE_RESPONSE_CONVERTER = new SolicitudProyectoSgeConverter();
