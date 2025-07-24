import { I18N_FIELD_RESPONSE_CONVERTER } from '@core/i18n/i18n-field.converter';
import { ITipoHito } from '@core/models/csp/tipos-configuracion';
import { SgiBaseConverter } from '@sgi/framework/core';
import { ITipoHitoResponse } from './tipo-hito-response';

class TipoHitoResponseConverter extends SgiBaseConverter<ITipoHitoResponse, ITipoHito> {
  toTarget(value: ITipoHitoResponse): ITipoHito {
    if (!value) {
      return value as unknown as ITipoHito;
    }
    return {
      id: value.id,
      nombre: value.nombre ? I18N_FIELD_RESPONSE_CONVERTER.toTargetArray(value.nombre) : [],
      descripcion: value.descripcion ? I18N_FIELD_RESPONSE_CONVERTER.toTargetArray(value.descripcion) : [],
      activo: value.activo
    };
  }
  fromTarget(value: ITipoHito): ITipoHitoResponse {
    if (!value) {
      return value as unknown as ITipoHitoResponse;
    }
    return {
      id: value.id,
      nombre: value.nombre ? I18N_FIELD_RESPONSE_CONVERTER.fromTargetArray(value.nombre) : [],
      descripcion: value.descripcion ? I18N_FIELD_RESPONSE_CONVERTER.fromTargetArray(value.descripcion) : [],
      activo: value.activo
    };
  }
}

export const TIPO_HITO_RESPONSE_CONVERTER = new TipoHitoResponseConverter();
