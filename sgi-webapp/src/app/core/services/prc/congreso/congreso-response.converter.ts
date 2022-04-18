import { ICongreso } from '@core/models/prc/congreso';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { SgiBaseConverter } from '@sgi/framework/core';
import { ICongresoResponse } from './congreso-response';

class CongresoResponseConverter extends SgiBaseConverter<ICongresoResponse, ICongreso>{
  toTarget(value: ICongresoResponse): ICongreso {
    if (!value) {
      return value as unknown as ICongreso;
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
      fechaCelebracion: LuxonUtils.fromBackend(value.fechaCelebracion),
      produccionCientificaRef: value.produccionCientificaRef,
      tipoEvento: value.tipoEvento,
      tituloTrabajo: value.tituloTrabajo
    };
  }
  fromTarget(value: ICongreso): ICongresoResponse {
    if (!value) {
      return value as unknown as ICongresoResponse;
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
      fechaCelebracion: LuxonUtils.toBackend(value.fechaCelebracion),
      produccionCientificaRef: value.produccionCientificaRef,
      tipoEvento: value.tipoEvento,
      tituloTrabajo: value.tituloTrabajo
    };
  }
}

export const CONGRESO_RESPONSE_CONVERTER = new CongresoResponseConverter();
