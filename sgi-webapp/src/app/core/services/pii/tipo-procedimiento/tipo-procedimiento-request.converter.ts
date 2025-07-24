import { I18N_FIELD_REQUEST_CONVERTER } from '@core/i18n/i18n-field.converter';
import { ITipoProcedimiento } from '@core/models/pii/tipo-procedimiento';
import { SgiBaseConverter } from '@sgi/framework/core';
import { ITipoProcedimientoRequest } from './tipo-procedimiento-request';

export class TipoProcedimientoRequestConverter extends SgiBaseConverter<ITipoProcedimientoRequest, ITipoProcedimiento> {

  toTarget(value: ITipoProcedimientoRequest): ITipoProcedimiento {

    if (!value) {
      return value as unknown as ITipoProcedimiento;
    }

    return {
      id: undefined,
      activo: true,
      nombre: value.nombre ? I18N_FIELD_REQUEST_CONVERTER.toTargetArray(value.nombre) : [],
      descripcion: value.descripcion ? I18N_FIELD_REQUEST_CONVERTER.toTargetArray(value.descripcion) : []
    };
  }

  fromTarget(value: ITipoProcedimiento): ITipoProcedimientoRequest {

    if (!value) {
      return value as unknown as ITipoProcedimientoRequest;
    }

    return {
      nombre: value.nombre ? I18N_FIELD_REQUEST_CONVERTER.fromTargetArray(value.nombre) : [],
      descripcion: value.descripcion ? I18N_FIELD_REQUEST_CONVERTER.fromTargetArray(value.descripcion) : []
    };
  }
}

export const TIPO_PROCEDIMIENTO_REQUEST_CONVERTER = new TipoProcedimientoRequestConverter();
