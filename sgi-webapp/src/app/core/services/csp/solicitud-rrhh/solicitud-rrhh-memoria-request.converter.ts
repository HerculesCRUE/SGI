import { ISolicitudRrhhMemoria } from '@core/models/csp/solicitud-rrhh-memoria';
import { SgiBaseConverter } from '@sgi/framework/core';
import { ISolicitudRrhhMemoriaRequest } from './solicitud-rrhh-memoria-request';

class SolicitudRrhhMemoriaRequestConverter
  extends SgiBaseConverter<ISolicitudRrhhMemoriaRequest, ISolicitudRrhhMemoria> {
  toTarget(value: ISolicitudRrhhMemoriaRequest): ISolicitudRrhhMemoria {
    if (!value) {
      return value as unknown as ISolicitudRrhhMemoria;
    }
    return {
      tituloTrabajo: value.tituloTrabajo,
      observaciones: value.observaciones,
      resumen: value.resumen
    };
  }

  fromTarget(value: ISolicitudRrhhMemoria): ISolicitudRrhhMemoriaRequest {
    if (!value) {
      return value as unknown as ISolicitudRrhhMemoriaRequest;
    }
    return {
      tituloTrabajo: value.tituloTrabajo,
      observaciones: value.observaciones,
      resumen: value.resumen
    };
  }
}

export const SOLICITUD_RRHH_MEMORIA_REQUEST_CONVERTER = new SolicitudRrhhMemoriaRequestConverter();
