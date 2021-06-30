import { ITipoProteccion } from '@core/models/pii/tipo-proteccion';
import { SgiBaseConverter } from '@sgi/framework/core';
import { ITipoProteccionRequest } from './tipo-proteccion-request';

class TipoProteccionRequestConverter extends SgiBaseConverter<ITipoProteccionRequest, ITipoProteccion>{
  toTarget(value: ITipoProteccionRequest): ITipoProteccion {
    if (!value) {
      return value as unknown as ITipoProteccion;
    }
    return {
      id: undefined,
      nombre: value.nombre,
      descripcion: value.descripcion,
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
      nombre: value.nombre,
      descripcion: value.descripcion,
      tipoPropiedad: value.tipoPropiedad,
      padreId: value.padre?.id
    };
  }
}

export const TIPO_PROTECCION_REQUEST_CONVERTER = new TipoProteccionRequestConverter();
