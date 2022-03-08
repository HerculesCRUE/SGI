import { ICampoProduccionCientifica } from '@core/models/prc/campo-produccion-cientifica';
import { IValorCampo } from '@core/models/prc/valor-campo';
import { SgiBaseConverter } from '@sgi/framework/core';
import { IValorCampoResponse } from './valor-campo-response';

class ValorCampoResponseConverter extends SgiBaseConverter<IValorCampoResponse, IValorCampo>{
  toTarget(value: IValorCampoResponse): IValorCampo {
    if (!value) {
      return value as unknown as IValorCampo;
    }
    return {
      id: value.id,
      campoProduccionCientifica: value.campoProduccionCientificaId !== null ?
        { id: value.campoProduccionCientificaId } as ICampoProduccionCientifica : null,
      orden: value.orden,
      valor: value.valor
    };
  }
  fromTarget(value: IValorCampo): IValorCampoResponse {
    if (!value) {
      return value as unknown as IValorCampoResponse;
    }
    return {
      id: value.id,
      campoProduccionCientificaId: value.campoProduccionCientifica?.id,
      orden: value.orden,
      valor: value.valor
    };
  }
}

export const VALOR_CAMPO_RESPONSE_CONVERTER = new ValorCampoResponseConverter();
