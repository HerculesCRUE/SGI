import { IAutorizacion } from '@core/models/csp/autorizacion';
import { IEstadoAutorizacion } from '@core/models/csp/estado-autorizacion';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { SgiBaseConverter } from '@sgi/framework/core';
import { IEstadoAutorizacionResponse } from './estado-autorizacion-response';

class EstadoAutorizacionResponseConverter
  extends SgiBaseConverter<IEstadoAutorizacionResponse, IEstadoAutorizacion> {
  toTarget(value: IEstadoAutorizacionResponse): IEstadoAutorizacion {
    if (!value) {
      return value as unknown as IEstadoAutorizacion;
    }
    return {
      id: value.id,
      autorizacion: {
        id: value.autorizacionId
      } as IAutorizacion,
      comentario: value.comentario,
      fecha: LuxonUtils.fromBackend(value.fecha),
      estado: value.estado
    };
  }

  fromTarget(value: IEstadoAutorizacion): IEstadoAutorizacionResponse {
    if (!value) {
      return value as unknown as IEstadoAutorizacionResponse;
    }
    return {
      id: value.id,
      autorizacionId: value.autorizacion?.id,
      comentario: value.comentario,
      fecha: LuxonUtils.toBackend(value.fecha),
      estado: value.estado
    };
  }
}

export const ESTADO_AUTORIZACION_RESPONSE_CONVERTER = new EstadoAutorizacionResponseConverter();
