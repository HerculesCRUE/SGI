import { ITipoFinalidad } from '@core/models/csp/tipos-configuracion';
import { SgiBaseConverter } from '@sgi/framework/core';
import { ITipoFinalidadResponse } from './tipo-finalidad-response';
import { I18N_FIELD_RESPONSE_CONVERTER } from '@core/i18n/i18n-field.converter';

class TipoFinalidadResponseConverter extends SgiBaseConverter<ITipoFinalidadResponse, ITipoFinalidad> {
  toTarget(value: ITipoFinalidadResponse): ITipoFinalidad {
    if (!value) {
      return value as unknown as ITipoFinalidad;
    }
    return {
      id: value.id,
      nombre: value.nombre ? I18N_FIELD_RESPONSE_CONVERTER.toTargetArray(value.nombre) : [],
      descripcion: value.descripcion ? I18N_FIELD_RESPONSE_CONVERTER.toTargetArray(value.descripcion) : [],
      activo: value.activo
    };
  }
  fromTarget(value: ITipoFinalidad): ITipoFinalidadResponse {
    if (!value) {
      return value as unknown as ITipoFinalidadResponse;
    }
    return {
      id: value.id,
      nombre: value.nombre ? I18N_FIELD_RESPONSE_CONVERTER.fromTargetArray(value.nombre) : [],
      descripcion: value.descripcion ? I18N_FIELD_RESPONSE_CONVERTER.fromTargetArray(value.descripcion) : [],
      activo: value.activo
    };
  }
}

export const TIPO_FINALIDAD_RESPONSE_CONVERTER = new TipoFinalidadResponseConverter();
