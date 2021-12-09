import { IInvencion } from '@core/models/pii/invencion';
import { IInvencionPalabraClave } from '@core/models/pii/invencion-palabra-clave';
import { SgiBaseConverter } from '@sgi/framework/core';
import { IInvencionPalabraClaveResponse } from './invencion-palabra-clave-response';

class IInvencionPalabraClaveResponseConverter extends SgiBaseConverter<IInvencionPalabraClaveResponse, IInvencionPalabraClave> {

  toTarget(value: IInvencionPalabraClaveResponse): IInvencionPalabraClave {
    if (!value) {
      return value as unknown as IInvencionPalabraClave;
    }

    return {
      id: value.id,
      invencion: { id: value.invencionId } as IInvencion,
      palabraClave: value.palabraClaveRef
    };
  }

  fromTarget(value: IInvencionPalabraClave): IInvencionPalabraClaveResponse {
    if (!value) {
      return value as unknown as IInvencionPalabraClaveResponse;
    }

    return {
      id: value.id,
      invencionId: value.invencion?.id,
      palabraClaveRef: value.palabraClave
    };
  }
}

export const INVENCION_PALABRACLAVE_RESPONSE_CONVERTER = new IInvencionPalabraClaveResponseConverter();
