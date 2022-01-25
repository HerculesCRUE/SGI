import { ISolicitud } from '@core/models/csp/solicitud';
import { ISolicitudPalabraClave } from '@core/models/csp/solicitud-palabra-clave';
import { SgiBaseConverter } from '@sgi/framework/core';
import { ISolicitudPalabraClaveRequest } from './solicitud-palabra-clave-request';

class ISolicitudPalabraClaveRequestConverter extends SgiBaseConverter<ISolicitudPalabraClaveRequest, ISolicitudPalabraClave> {

  toTarget(value: ISolicitudPalabraClaveRequest): ISolicitudPalabraClave {
    if (!value) {
      return value as unknown as ISolicitudPalabraClave;
    }

    return {
      id: undefined,
      solicitud: { id: value.solicitudId } as ISolicitud,
      palabraClave: value.palabraClaveRef
    };
  }

  fromTarget(value: ISolicitudPalabraClave): ISolicitudPalabraClaveRequest {
    if (!value) {
      return value as unknown as ISolicitudPalabraClaveRequest;
    }

    return {
      solicitudId: value.solicitud?.id,
      palabraClaveRef: value.palabraClave
    };
  }
}

export const SOLICITUD_PALABRACLAVE_REQUEST_CONVERTER = new ISolicitudPalabraClaveRequestConverter();
