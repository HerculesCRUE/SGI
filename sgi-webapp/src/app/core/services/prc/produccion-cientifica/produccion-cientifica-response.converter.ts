import { IProduccionCientifica } from '@core/models/prc/produccion-cientifica';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { SgiBaseConverter } from '@sgi/framework/core';
import { IProduccionCientificaResponse } from './produccion-cientifica-response';

class ProduccionCientificaResponseConverter extends SgiBaseConverter<IProduccionCientificaResponse, IProduccionCientifica>{
  toTarget(value: IProduccionCientificaResponse): IProduccionCientifica {
    if (!value) {
      return value as unknown as IProduccionCientifica;
    }
    return {
      id: value.id,
      epigrafe: value.epigrafe,
      estado: value.estado ? {
        id: value.estado.id,
        comentario: value.estado.comentario,
        estado: value.estado.estado,
        fecha: LuxonUtils.fromBackend(value.estado.fecha)
      } : null,
      produccionCientificaRef: value.produccionCientificaRef,
    };
  }
  fromTarget(value: IProduccionCientifica): IProduccionCientificaResponse {
    if (!value) {
      return value as unknown as IProduccionCientificaResponse;
    }
    return {
      id: value.id,
      epigrafe: value.epigrafe,
      estado: value.estado ? {
        id: value.estado.id,
        comentario: value.estado.comentario,
        estado: value.estado.estado,
        fecha: LuxonUtils.toBackend(value.estado.fecha)
      } : null,
      produccionCientificaRef: value.produccionCientificaRef,
    };
  }
}

export const PRODUCCION_CIENTIFICA_RESPONSE_CONVERTER = new ProduccionCientificaResponseConverter();
