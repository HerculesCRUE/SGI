import { I18N_FIELD_RESPONSE_CONVERTER } from '@core/i18n/i18n-field.converter';
import { ITipoEnlace } from '@core/models/csp/tipos-configuracion';
import { SgiBaseConverter } from '@sgi/framework/core';
import { ITipoEnlaceResponse } from './tipo-enlace-response';

class TipoEnlaceResponseConverter extends SgiBaseConverter<ITipoEnlaceResponse, ITipoEnlace> {
  toTarget(value: ITipoEnlaceResponse): ITipoEnlace {
    if (!value) {
      return value as unknown as ITipoEnlace;
    }
    return {
      id: value.id,
      nombre: value.nombre ? I18N_FIELD_RESPONSE_CONVERTER.toTargetArray(value.nombre) : [],
      descripcion: value.descripcion ? I18N_FIELD_RESPONSE_CONVERTER.toTargetArray(value.descripcion) : [],
      activo: value.activo
    };
  }
  fromTarget(value: ITipoEnlace): ITipoEnlaceResponse {
    if (!value) {
      return value as unknown as ITipoEnlaceResponse;
    }
    return {
      id: value.id,
      nombre: value.nombre ? I18N_FIELD_RESPONSE_CONVERTER.fromTargetArray(value.nombre) : [],
      descripcion: value.descripcion ? I18N_FIELD_RESPONSE_CONVERTER.fromTargetArray(value.descripcion) : [],
      activo: value.activo
    };
  }
}

export const TIPO_ENLACE_RESPONSE_CONVERTER = new TipoEnlaceResponseConverter();
