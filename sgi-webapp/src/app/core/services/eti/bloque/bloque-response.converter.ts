import { I18N_FIELD_RESPONSE_CONVERTER } from '@core/i18n/i18n-field.converter';
import { IBloque } from '@core/models/eti/bloque';
import { SgiBaseConverter } from '@sgi/framework/core';
import { FORMULARIO_RESPONSE_CONVERTER } from '../formulario/formulario-response.converter';
import { IBloqueResponse } from './bloque-response';

class BloqueResponseConverter
  extends SgiBaseConverter<IBloqueResponse, IBloque> {
  toTarget(value: IBloqueResponse): IBloque {
    if (!value) {
      return value as unknown as IBloque;
    }
    return {
      formulario: FORMULARIO_RESPONSE_CONVERTER.toTarget(value.formulario),
      id: value.id,
      nombre: value.nombre ? I18N_FIELD_RESPONSE_CONVERTER.toTargetArray(value.nombre) : [],
      orden: value.orden
    };
  }

  fromTarget(value: IBloque): IBloqueResponse {
    if (!value) {
      return value as unknown as IBloqueResponse;
    }
    return {
      formulario: FORMULARIO_RESPONSE_CONVERTER.fromTarget(value.formulario),
      id: value.id,
      nombre: value.nombre ? I18N_FIELD_RESPONSE_CONVERTER.fromTargetArray(value.nombre) : null,
      orden: value.orden
    };
  }
}

export const BLOQUE_RESPONSE_CONVERTER = new BloqueResponseConverter();
