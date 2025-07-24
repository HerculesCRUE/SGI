import { I18N_FIELD_REQUEST_CONVERTER } from '@core/i18n/i18n-field.converter';
import { ITipoOrigenFuenteFinanciacion } from '@core/models/csp/tipos-configuracion';
import { SgiBaseConverter } from '@sgi/framework/core';
import { ITipoOrigenFuenteFinanciacionRequest } from './tipo-origen-fuente-financiacion-request';

class TipoOrigenFuenteFinanciacionRequestConverter extends SgiBaseConverter<ITipoOrigenFuenteFinanciacionRequest, ITipoOrigenFuenteFinanciacion> {
  toTarget(value: ITipoOrigenFuenteFinanciacionRequest): ITipoOrigenFuenteFinanciacion {
    if (!value) {
      return value as unknown as ITipoOrigenFuenteFinanciacion;
    }
    return {
      id: undefined,
      nombre: value.nombre ? I18N_FIELD_REQUEST_CONVERTER.toTargetArray(value.nombre) : [],
      activo: true
    };
  }
  fromTarget(value: ITipoOrigenFuenteFinanciacion): ITipoOrigenFuenteFinanciacionRequest {
    if (!value) {
      return value as unknown as ITipoOrigenFuenteFinanciacionRequest;
    }
    return {
      nombre: value.nombre ? I18N_FIELD_REQUEST_CONVERTER.fromTargetArray(value.nombre) : [],
    };
  }
}

export const TIPO_ORIGEN_FUENTE_FINANCIACION_REQUEST_CONVERTER = new TipoOrigenFuenteFinanciacionRequestConverter();
