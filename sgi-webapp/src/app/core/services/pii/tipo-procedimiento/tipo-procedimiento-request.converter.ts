import { ITipoProcedimiento } from '@core/models/pii/tipo-procedimiento';
import { SgiBaseConverter } from '@sgi/framework/core';
import { ITipoProcedimientoRequest } from './tipo-procedimiento-request';

export class TipoProcedimientoRequestConverter extends SgiBaseConverter<ITipoProcedimientoRequest, ITipoProcedimiento> {

  toTarget(value: ITipoProcedimientoRequest): ITipoProcedimiento {

    if (!value) {
      return value as unknown as ITipoProcedimiento;
    }

    return { id: undefined, activo: true, nombre: value.nombre, descripcion: value.descripcion };
  }

  fromTarget(value: ITipoProcedimiento): ITipoProcedimientoRequest {

    if (!value) {
      return value as unknown as ITipoProcedimientoRequest;
    }

    return {
      nombre: value.nombre,
      descripcion: value.descripcion
    };
  }
}

export const TIPO_PROCEDIMIENTO_REQUEST_CONVERTER = new TipoProcedimientoRequestConverter();
