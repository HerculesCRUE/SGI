import { IActividad } from '@core/models/prc/actividad';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { SgiBaseConverter } from '@sgi/framework/core';
import { IActividadResponse } from './actividad-response';


class ActividadResponseConverter extends SgiBaseConverter<IActividadResponse, IActividad>{
  toTarget(value: IActividadResponse): IActividad {
    if (!value) {
      return value as unknown as IActividad;
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
      fechaInicio: LuxonUtils.fromBackend(value.fechaInicio),
      produccionCientificaRef: value.produccionCientificaRef,
      tituloActividad: value.tituloActividad,
    };
  }
  fromTarget(value: IActividad): IActividadResponse {
    if (!value) {
      return value as unknown as IActividadResponse;
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
      fechaInicio: LuxonUtils.toBackend(value.fechaInicio),
      produccionCientificaRef: value.produccionCientificaRef,
      tituloActividad: value.tituloActividad,
    };
  }
}

export const ACTIVIDAD_RESPONSE_CONVERTER = new ActividadResponseConverter();
