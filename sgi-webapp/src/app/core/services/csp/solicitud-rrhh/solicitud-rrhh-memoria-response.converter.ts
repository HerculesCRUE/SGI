import { ISolicitudRrhhMemoria } from '@core/models/csp/solicitud-rrhh-memoria';
import { SgiBaseConverter } from '@sgi/framework/core';
import { ISolicitudRrhhMemoriaResponse } from './solicitud-rrhh-memoria-response';

class SolicitudRrhhMemoriaResponseConverter
  extends SgiBaseConverter<ISolicitudRrhhMemoriaResponse, ISolicitudRrhhMemoria> {
  toTarget(value: ISolicitudRrhhMemoriaResponse): ISolicitudRrhhMemoria {
    if (!value) {
      return value as unknown as ISolicitudRrhhMemoria;
    }
    return {
      tituloTrabajo: value.tituloTrabajo,
      observaciones: value.observaciones,
      resumen: value.resumen
    };
  }

  fromTarget(value: ISolicitudRrhhMemoria): ISolicitudRrhhMemoriaResponse {
    if (!value) {
      return value as unknown as ISolicitudRrhhMemoriaResponse;
    }
    return {
      tituloTrabajo: value.tituloTrabajo,
      observaciones: value.observaciones,
      resumen: value.resumen
    };
  }
}

export const SOLICITUD_RRHH_MEMORIA_RESPONSE_CONVERTER = new SolicitudRrhhMemoriaResponseConverter();
