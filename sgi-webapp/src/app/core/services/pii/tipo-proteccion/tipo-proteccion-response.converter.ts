import { I18N_FIELD_RESPONSE_CONVERTER } from '@core/i18n/i18n-field.converter';
import { ITipoProteccion } from '@core/models/pii/tipo-proteccion';
import { SgiBaseConverter } from '@sgi/framework/core';
import { ITipoProteccionResponse } from './tipo-proteccion-response';

class TipoProteccionResponseConverter extends SgiBaseConverter<ITipoProteccionResponse, ITipoProteccion> {
  toTarget(value: ITipoProteccionResponse): ITipoProteccion {
    if (!value) {
      return value as unknown as ITipoProteccion;
    }
    return {
      id: value.id,
      nombre: value.nombre ? I18N_FIELD_RESPONSE_CONVERTER.toTargetArray(value.nombre) : [],
      descripcion: value.descripcion ? I18N_FIELD_RESPONSE_CONVERTER.toTargetArray(value.descripcion) : [],
      tipoPropiedad: value.tipoPropiedad,
      padre: value.padre ? TIPO_PROTECCION_RESPONSE_CONVERTER.toTarget(value.padre) : null,
      activo: value.activo
    };
  }
  fromTarget(value: ITipoProteccion): ITipoProteccionResponse {
    if (!value) {
      return value as unknown as ITipoProteccionResponse;
    }
    return {
      id: value.id,
      nombre: value.nombre ? I18N_FIELD_RESPONSE_CONVERTER.fromTargetArray(value.nombre) : [],
      descripcion: value.descripcion ? I18N_FIELD_RESPONSE_CONVERTER.fromTargetArray(value.descripcion) : [],
      tipoPropiedad: value.tipoPropiedad,
      padre: value.padre ? TIPO_PROTECCION_RESPONSE_CONVERTER.fromTarget(value.padre) : null,
      activo: value.activo
    };
  }
}

export const TIPO_PROTECCION_RESPONSE_CONVERTER = new TipoProteccionResponseConverter();
