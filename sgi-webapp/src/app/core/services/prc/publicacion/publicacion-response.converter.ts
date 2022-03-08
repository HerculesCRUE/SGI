import { IPublicacion } from '@core/models/prc/publicacion';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { SgiBaseConverter } from '@sgi/framework/core';
import { IPublicacionResponse } from './publicacion-response';

class PublicacionResponseConverter extends SgiBaseConverter<IPublicacionResponse, IPublicacion>{
  toTarget(value: IPublicacionResponse): IPublicacion {
    if (!value) {
      return value as unknown as IPublicacion;
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
      fechaPublicacion: LuxonUtils.fromBackend(value.fechaPublicacion),
      produccionCientificaRef: value.produccionCientificaRef,
      tipoProduccion: value.tipoProduccion,
      tituloPublicacion: value.tituloPublicacion
    };
  }
  fromTarget(value: IPublicacion): IPublicacionResponse {
    if (!value) {
      return value as unknown as IPublicacionResponse;
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
      fechaPublicacion: LuxonUtils.toBackend(value.fechaPublicacion),
      produccionCientificaRef: value.produccionCientificaRef,
      tipoProduccion: value.tipoProduccion,
      tituloPublicacion: value.tituloPublicacion
    };
  }
}

export const PUBLICACION_RESPONSE_CONVERTER = new PublicacionResponseConverter();
