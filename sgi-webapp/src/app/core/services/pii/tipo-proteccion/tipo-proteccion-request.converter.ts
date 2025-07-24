import { ITipoProteccion } from '@core/models/pii/tipo-proteccion';
import { SgiBaseConverter } from '@sgi/framework/core';
import { ITipoProteccionRequest } from './tipo-proteccion-request';
import { I18N_FIELD_REQUEST_CONVERTER } from '@core/i18n/i18n-field.converter';

class TipoProteccionRequestConverter extends SgiBaseConverter<ITipoProteccionRequest, ITipoProteccion> {
  toTarget(value: ITipoProteccionRequest): ITipoProteccion {
    if (!value) {
      return value as unknown as ITipoProteccion;
    }
    return {
      id: undefined,
      nombre: value.nombre ? I18N_FIELD_REQUEST_CONVERTER.toTargetArray(value.nombre) : [],
      descripcion: value.descripcion ? I18N_FIELD_REQUEST_CONVERTER.toTargetArray(value.descripcion) : [],
      tipoPropiedad: value.tipoPropiedad,
      padre: { id: value.padreId } as ITipoProteccion,
      activo: true
    };
  }
  fromTarget(value: ITipoProteccion): ITipoProteccionRequest {
    if (!value) {
      return value as unknown as ITipoProteccionRequest;
    }
    return {
      id: value.id,
      nombre: value.nombre ? I18N_FIELD_REQUEST_CONVERTER.fromTargetArray(value.nombre) : [],
      descripcion: value.descripcion ? I18N_FIELD_REQUEST_CONVERTER.fromTargetArray(value.descripcion) : [],
      tipoPropiedad: value.tipoPropiedad,
      padreId: value.padre?.id
    };
  }
}

export const TIPO_PROTECCION_REQUEST_CONVERTER = new TipoProteccionRequestConverter();
