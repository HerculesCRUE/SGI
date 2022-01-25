import { IConvocatoria } from '@core/models/csp/convocatoria';
import { IConvocatoriaPalabraClave } from '@core/models/csp/convocatoria-palabra-clave';
import { SgiBaseConverter } from '@sgi/framework/core';
import { IConvocatoriaPalabraClaveResponse } from './convocatoria-palabra-clave-response';

class IConvocatoriaPalabraClaveResponseConverter extends SgiBaseConverter<IConvocatoriaPalabraClaveResponse, IConvocatoriaPalabraClave> {

  toTarget(value: IConvocatoriaPalabraClaveResponse): IConvocatoriaPalabraClave {
    if (!value) {
      return value as unknown as IConvocatoriaPalabraClave;
    }

    return {
      id: value.id,
      convocatoria: { id: value.convocatoriaId } as IConvocatoria,
      palabraClave: value.palabraClaveRef
    };
  }

  fromTarget(value: IConvocatoriaPalabraClave): IConvocatoriaPalabraClaveResponse {
    if (!value) {
      return value as unknown as IConvocatoriaPalabraClaveResponse;
    }

    return {
      id: value.id,
      convocatoriaId: value.convocatoria?.id,
      palabraClaveRef: value.palabraClave
    };
  }
}

export const CONVOCATORIA_PALABRACLAVE_RESPONSE_CONVERTER = new IConvocatoriaPalabraClaveResponseConverter();
