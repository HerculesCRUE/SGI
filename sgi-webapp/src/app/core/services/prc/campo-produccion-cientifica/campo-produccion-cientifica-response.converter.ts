import { ICampoProduccionCientifica } from '@core/models/prc/campo-produccion-cientifica';
import { IProduccionCientifica } from '@core/models/prc/produccion-cientifica';
import { SgiBaseConverter } from '@sgi/framework/core';
import { ICampoProduccionCientificaResponse } from './campo-produccion-cientifica-response';

class CampoProduccionCientificaResponseConverter extends SgiBaseConverter<ICampoProduccionCientificaResponse, ICampoProduccionCientifica>{
  toTarget(value: ICampoProduccionCientificaResponse): ICampoProduccionCientifica {
    if (!value) {
      return value as unknown as ICampoProduccionCientifica;
    }
    return {
      id: value.id,
      codigo: value.codigo,
      produccionCientifica: value.produccionCientificaId !== null ?
        { id: value.produccionCientificaId } as IProduccionCientifica : null
    };
  }
  fromTarget(value: ICampoProduccionCientifica): ICampoProduccionCientificaResponse {
    if (!value) {
      return value as unknown as ICampoProduccionCientificaResponse;
    }
    return {
      id: value.id,
      codigo: value.codigo,
      produccionCientificaId: value.produccionCientifica?.id
    };
  }
}

export const CAMPO_PRODUCCION_CIENTIFICA_RESPONSE_CONVERTER = new CampoProduccionCientificaResponseConverter();
