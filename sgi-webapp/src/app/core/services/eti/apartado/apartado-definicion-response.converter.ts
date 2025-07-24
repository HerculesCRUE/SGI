import { Language } from '@core/i18n/language';
import { IApartadoDefinion } from '@core/models/eti/apartado';
import { SgiBaseConverter } from '@sgi/framework/core';
import { IApartadoDefinicionResponse } from './apartado-definicion-response';

class ApartadoDefinicionResponseConverter
  extends SgiBaseConverter<IApartadoDefinicionResponse, IApartadoDefinion> {
  toTarget(value: IApartadoDefinicionResponse): IApartadoDefinion {
    if (!value) {
      return value as unknown as IApartadoDefinion;
    }
    return {
      esquema: value.esquema,
      lang: Language.fromCode(value.lang),
      nombre: value.nombre
    };
  }

  fromTarget(value: IApartadoDefinion): IApartadoDefinicionResponse {
    if (!value) {
      return value as unknown as IApartadoDefinicionResponse;
    }
    return {
      esquema: value.esquema,
      lang: value.lang?.code,
      nombre: value.nombre
    };
  }
}

export const APARTADO_DEFINICION_RESPONSE_CONVERTER = new ApartadoDefinicionResponseConverter();
