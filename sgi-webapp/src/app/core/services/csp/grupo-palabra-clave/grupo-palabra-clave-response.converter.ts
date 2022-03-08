import { IGrupo } from '@core/models/csp/grupo';
import { IGrupoPalabraClave } from '@core/models/csp/grupo-palabra-clave';
import { SgiBaseConverter } from '@sgi/framework/core';
import { IGrupoPalabraClaveResponse } from './grupo-palabra-clave-response';

class IGrupoPalabraClaveResponseConverter extends SgiBaseConverter<IGrupoPalabraClaveResponse, IGrupoPalabraClave> {

  toTarget(value: IGrupoPalabraClaveResponse): IGrupoPalabraClave {
    if (!value) {
      return value as unknown as IGrupoPalabraClave;
    }

    return {
      id: value.id,
      grupo: { id: value.grupoId } as IGrupo,
      palabraClave: value.palabraClaveRef
    };
  }

  fromTarget(value: IGrupoPalabraClave): IGrupoPalabraClaveResponse {
    if (!value) {
      return value as unknown as IGrupoPalabraClaveResponse;
    }

    return {
      id: value.id,
      grupoId: value.grupo?.id,
      palabraClaveRef: value.palabraClave
    };
  }
}

export const GRUPO_PALABRACLAVE_RESPONSE_CONVERTER = new IGrupoPalabraClaveResponseConverter();
