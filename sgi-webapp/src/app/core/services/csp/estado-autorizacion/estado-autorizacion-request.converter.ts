import { IAutorizacion } from '@core/models/csp/autorizacion';
import { IEstadoAutorizacion } from '@core/models/csp/estado-autorizacion';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { SgiBaseConverter } from '@sgi/framework/core';
import { IEstadoAutorizacionRequest } from './estado-autorizacion-request';

class EstadoAutorizacionRequestConverter
  extends SgiBaseConverter<IEstadoAutorizacionRequest, IEstadoAutorizacion> {
  toTarget(value: IEstadoAutorizacionRequest): IEstadoAutorizacion {
    if (!value) {
      return value as unknown as IEstadoAutorizacion;
    }
    return {
      id: undefined,
      autorizacion: {
        id: value.autorizacionId
      } as IAutorizacion,
      comentario: value.comentario,
      fecha: LuxonUtils.fromBackend(value.fecha),
      estado: value.estado
    };
  }

  fromTarget(value: IEstadoAutorizacion): IEstadoAutorizacionRequest {
    if (!value) {
      return value as unknown as IEstadoAutorizacionRequest;
    }
    return {
      autorizacionId: value.autorizacion?.id,
      comentario: value.comentario,
      fecha: LuxonUtils.toBackend(value.fecha),
      estado: value.estado
    };
  }
}

export const ESTADO_AUTORIZACION_REQUEST_CONVERTER = new EstadoAutorizacionRequestConverter();
