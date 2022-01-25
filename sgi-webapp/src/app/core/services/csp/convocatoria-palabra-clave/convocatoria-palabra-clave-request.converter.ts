import { IConvocatoria } from '@core/models/csp/convocatoria';
import { IConvocatoriaPalabraClave } from '@core/models/csp/convocatoria-palabra-clave';
import { SgiBaseConverter } from '@sgi/framework/core';
import { IConvocatoriaPalabraClaveRequest } from './convocatoria-palabra-clave-request';

class IConvocatoriaPalabraClaveRequestConverter extends SgiBaseConverter<IConvocatoriaPalabraClaveRequest, IConvocatoriaPalabraClave> {

  toTarget(value: IConvocatoriaPalabraClaveRequest): IConvocatoriaPalabraClave {
    if (!value) {
      return value as unknown as IConvocatoriaPalabraClave;
    }

    return {
      id: undefined,
      convocatoria: { id: value.convocatoriaId } as IConvocatoria,
      palabraClave: value.palabraClaveRef
    };
  }

  fromTarget(value: IConvocatoriaPalabraClave): IConvocatoriaPalabraClaveRequest {
    if (!value) {
      return value as unknown as IConvocatoriaPalabraClaveRequest;
    }

    return {
      convocatoriaId: value.convocatoria?.id,
      palabraClaveRef: value.palabraClave
    };
  }
}

export const CONVOCATORIA_PALABRACLAVE_REQUEST_CONVERTER = new IConvocatoriaPalabraClaveRequestConverter();
