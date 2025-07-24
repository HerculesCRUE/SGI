import { IApartado } from '@core/models/eti/apartado';
import { SgiBaseConverter } from '@sgi/framework/core';
import { BLOQUE_RESPONSE_CONVERTER } from '../bloque/bloque-response.converter';
import { APARTADO_DEFINICION_RESPONSE_CONVERTER } from './apartado-definicion-response.converter';
import { IApartadoResponse } from './apartado-response';

class ApartadoResponseConverter
  extends SgiBaseConverter<IApartadoResponse, IApartado> {
  toTarget(value: IApartadoResponse): IApartado {
    if (!value) {
      return value as unknown as IApartado;
    }
    return {
      bloque: BLOQUE_RESPONSE_CONVERTER.toTarget(value.bloque),
      definicion: value.definicion ? APARTADO_DEFINICION_RESPONSE_CONVERTER.toTargetArray(value.definicion) : null,
      id: value.id,
      orden: value.orden,
      padre: value.padre ? APARTADO_RESPONSE_CONVERTER.toTarget(value.padre) : null
    };
  }

  fromTarget(value: IApartado): IApartadoResponse {
    if (!value) {
      return value as unknown as IApartadoResponse;
    }
    return {
      bloque: BLOQUE_RESPONSE_CONVERTER.fromTarget(value.bloque),
      definicion: value.definicion ? APARTADO_DEFINICION_RESPONSE_CONVERTER.fromTargetArray(value.definicion) : null,
      id: value.id,
      orden: value.orden,
      padre: value.padre ? APARTADO_RESPONSE_CONVERTER.fromTarget(value.padre) : null
    };
  }
}

export const APARTADO_RESPONSE_CONVERTER = new ApartadoResponseConverter();
