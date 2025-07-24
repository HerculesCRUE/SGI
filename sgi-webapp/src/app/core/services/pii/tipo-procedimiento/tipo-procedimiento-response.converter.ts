import { I18N_FIELD_RESPONSE_CONVERTER } from '@core/i18n/i18n-field.converter';
import { ITipoProcedimiento } from '@core/models/pii/tipo-procedimiento';
import { SgiBaseConverter } from '@sgi/framework/core';
import { ITipoProcedimientoResponse } from './tipo-procedimiento-response';

export class TipoProcedimientoResponseConverter extends SgiBaseConverter<ITipoProcedimientoResponse, ITipoProcedimiento> {

  toTarget(value: ITipoProcedimientoResponse): ITipoProcedimiento {

    if (!value) {
      return value as unknown as ITipoProcedimiento;
    }

    return {
      id: value.id,
      nombre: value.nombre ? I18N_FIELD_RESPONSE_CONVERTER.toTargetArray(value.nombre) : [],
      descripcion: value.descripcion ? I18N_FIELD_RESPONSE_CONVERTER.toTargetArray(value.descripcion) : [],
      activo: value.activo
    };
  }

  fromTarget(value: ITipoProcedimiento): ITipoProcedimientoResponse {

    if (!value) {
      return value as unknown as ITipoProcedimientoResponse;
    }

    return {
      id: value.id,
      nombre: value.nombre ? I18N_FIELD_RESPONSE_CONVERTER.fromTargetArray(value.nombre) : [],
      descripcion: value.descripcion ? I18N_FIELD_RESPONSE_CONVERTER.fromTargetArray(value.descripcion) : [],
      activo: value.activo
    };

  }
}

export const TIPO_PROCEDIMIENTO_RESPONSE_CONVERTER = new TipoProcedimientoResponseConverter();
