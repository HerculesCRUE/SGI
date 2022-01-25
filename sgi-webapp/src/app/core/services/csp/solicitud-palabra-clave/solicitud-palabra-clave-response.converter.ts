import { ISolicitud } from '@core/models/csp/solicitud';
import { ISolicitudPalabraClave } from '@core/models/csp/solicitud-palabra-clave';
import { SgiBaseConverter } from '@sgi/framework/core';
import { ISolicitudPalabraClaveResponse } from './solicitud-palabra-clave-response';

class ISolicitudPalabraClaveResponseConverter extends SgiBaseConverter<ISolicitudPalabraClaveResponse, ISolicitudPalabraClave> {

  toTarget(value: ISolicitudPalabraClaveResponse): ISolicitudPalabraClave {
    if (!value) {
      return value as unknown as ISolicitudPalabraClave;
    }

    return {
      id: value.id,
      solicitud: { id: value.solicitudId } as ISolicitud,
      palabraClave: value.palabraClaveRef
    };
  }

  fromTarget(value: ISolicitudPalabraClave): ISolicitudPalabraClaveResponse {
    if (!value) {
      return value as unknown as ISolicitudPalabraClaveResponse;
    }

    return {
      id: value.id,
      solicitudId: value.solicitud?.id,
      palabraClaveRef: value.palabraClave
    };
  }
}

export const SOLICITUD_PALABRACLAVE_RESPONSE_CONVERTER = new ISolicitudPalabraClaveResponseConverter();
