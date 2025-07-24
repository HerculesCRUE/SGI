import { I18N_FIELD_RESPONSE_CONVERTER } from '@core/i18n/i18n-field.converter';
import { ITipoOrigenFuenteFinanciacion } from '@core/models/csp/tipos-configuracion';
import { SgiBaseConverter } from '@sgi/framework/core';
import { ITipoOrigenFuenteFinanciacionResponse } from './tipo-origen-fuente-financiacion-response';

class TipoOrigenFuenteFinanciacionResponseConverter extends SgiBaseConverter<ITipoOrigenFuenteFinanciacionResponse, ITipoOrigenFuenteFinanciacion> {
  toTarget(value: ITipoOrigenFuenteFinanciacionResponse): ITipoOrigenFuenteFinanciacion {
    if (!value) {
      return value as unknown as ITipoOrigenFuenteFinanciacion;
    }
    return {
      id: value.id,
      nombre: value.nombre ? I18N_FIELD_RESPONSE_CONVERTER.toTargetArray(value.nombre) : [],
      activo: value.activo
    };
  }
  fromTarget(value: ITipoOrigenFuenteFinanciacion): ITipoOrigenFuenteFinanciacionResponse {
    if (!value) {
      return value as unknown as ITipoOrigenFuenteFinanciacionResponse;
    }
    return {
      id: value.id,
      nombre: value.nombre ? I18N_FIELD_RESPONSE_CONVERTER.fromTargetArray(value.nombre) : [],
      activo: value.activo
    };
  }
}

export const TIPO_ORIGEN_FUENTE_FINANCIACION_RESPONSE_CONVERTER = new TipoOrigenFuenteFinanciacionResponseConverter();
