import { I18N_FIELD_RESPONSE_CONVERTER } from '@core/i18n/i18n-field.converter';
import { ITipoFinalidad, ITipoFinanciacion } from '@core/models/csp/tipos-configuracion';
import { SgiBaseConverter } from '@sgi/framework/core';
import { ITipoFinanciacionResponse } from './tipo-financiacion-response';

class TipoFinanciacionResponseConverter extends SgiBaseConverter<ITipoFinanciacionResponse, ITipoFinanciacion> {
  toTarget(value: ITipoFinanciacionResponse): ITipoFinanciacion {
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
  fromTarget(value: ITipoFinanciacion): ITipoFinanciacionResponse {
    if (!value) {
      return value as unknown as ITipoFinanciacionResponse;
    }
    return {
      id: value.id,
      nombre: value.nombre ? I18N_FIELD_RESPONSE_CONVERTER.fromTargetArray(value.nombre) : [],
      descripcion: value.descripcion ? I18N_FIELD_RESPONSE_CONVERTER.fromTargetArray(value.descripcion) : [],
      activo: value.activo
    };
  }
}

export const TIPO_FINANCIACION_RESPONSE_CONVERTER = new TipoFinanciacionResponseConverter();
