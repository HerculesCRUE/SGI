import { ITipoCaducidad } from '@core/models/pii/tipo-caducidad';
import { SgiBaseConverter } from '@sgi/framework/core';
import { ITipoCaducidadResponse } from './tipo-caducidad-response';

export class TipoCaducidadResponseConverter extends SgiBaseConverter<ITipoCaducidadResponse, ITipoCaducidad> {

  toTarget(value: ITipoCaducidadResponse): ITipoCaducidad {
    return value ? {
      id: value.id,
      descripcion: value.descripcion
    } : (value as unknown as ITipoCaducidad);
  }

  fromTarget(value: ITipoCaducidad): ITipoCaducidadResponse {
    return value ? {
      id: value.id,
      descripcion: value.descripcion,
    } : (value as unknown as ITipoCaducidadResponse);
  }
}

export const TIPO_CADUCIDAD_RESPONSE_CONVERTER = new TipoCaducidadResponseConverter();
