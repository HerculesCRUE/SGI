import { IGrupo } from '@core/models/csp/grupo';
import { IGrupoPalabraClave } from '@core/models/csp/grupo-palabra-clave';
import { SgiBaseConverter } from '@sgi/framework/core';
import { IGrupoPalabraClaveRequest } from './grupo-palabra-clave-request';

class IGrupoPalabraClaveRequestConverter extends SgiBaseConverter<IGrupoPalabraClaveRequest, IGrupoPalabraClave> {

  toTarget(value: IGrupoPalabraClaveRequest): IGrupoPalabraClave {
    if (!value) {
      return value as unknown as IGrupoPalabraClave;
    }

    return {
      id: undefined,
      grupo: { id: value.grupoId } as IGrupo,
      palabraClave: value.palabraClaveRef
    };
  }

  fromTarget(value: IGrupoPalabraClave): IGrupoPalabraClaveRequest {
    if (!value) {
      return value as unknown as IGrupoPalabraClaveRequest;
    }

    return {
      grupoId: value.grupo?.id,
      palabraClaveRef: value.palabraClave
    };
  }
}

export const GRUPO_PALABRACLAVE_REQUEST_CONVERTER = new IGrupoPalabraClaveRequestConverter();
