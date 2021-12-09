import { IInvencion } from '@core/models/pii/invencion';
import { IInvencionPalabraClave } from '@core/models/pii/invencion-palabra-clave';
import { SgiBaseConverter } from '@sgi/framework/core';
import { IInvencionPalabraClaveRequest } from './invencion-palabra-clave-request';

class IInvencionPalabraClaveRequestConverter extends SgiBaseConverter<IInvencionPalabraClaveRequest, IInvencionPalabraClave> {

  toTarget(value: IInvencionPalabraClaveRequest): IInvencionPalabraClave {
    if (!value) {
      return value as unknown as IInvencionPalabraClave;
    }

    return {
      id: undefined,
      invencion: { id: value.invencionId } as IInvencion,
      palabraClave: value.palabraClaveRef
    };
  }

  fromTarget(value: IInvencionPalabraClave): IInvencionPalabraClaveRequest {
    if (!value) {
      return value as unknown as IInvencionPalabraClaveRequest;
    }

    return {
      invencionId: value.invencion?.id,
      palabraClaveRef: value.palabraClave
    };
  }
}

export const INVENCION_PALABRACLAVE_REQUEST_CONVERTER = new IInvencionPalabraClaveRequestConverter();
