import { I18N_FIELD_RESPONSE_CONVERTER } from '@core/i18n/i18n-field.converter';
import { ITipoRequerimiento } from '@core/models/csp/tipo-requerimiento';
import { SgiBaseConverter } from '@sgi/framework/core';
import { ITipoRequerimientoResponse } from './tipo-requerimiento-response';

class TipoRequerimientoResponseConverter
  extends SgiBaseConverter<ITipoRequerimientoResponse, ITipoRequerimiento> {
  toTarget(value: ITipoRequerimientoResponse): ITipoRequerimiento {
    if (!value) {
      return value as unknown as ITipoRequerimiento;
    }
    return {
      id: value.id,
      nombre: value.nombre ? I18N_FIELD_RESPONSE_CONVERTER.toTargetArray(value.nombre) : [],
      activo: value.activo
    };
  }
  fromTarget(value: ITipoRequerimiento): ITipoRequerimientoResponse {
    if (!value) {
      return value as unknown as ITipoRequerimientoResponse;
    }
    return {
      id: value.id,
      nombre: value.nombre ? I18N_FIELD_RESPONSE_CONVERTER.fromTargetArray(value.nombre) : [],
      activo: value.activo
    };
  }
}

export const TIPO_REQUERIMIENTO_RESPONSE_CONVERTER = new TipoRequerimientoResponseConverter();
