import { ITipoProteccion } from '@core/models/pii/tipo-proteccion';
import { SgiBaseConverter } from '@sgi/framework/core';
import { ITipoProteccionResponse } from './tipo-proteccion-response';

class TipoProteccionResponseConverter extends SgiBaseConverter<ITipoProteccionResponse, ITipoProteccion>{
  toTarget(value: ITipoProteccionResponse): ITipoProteccion {
    if (!value) {
      return value as unknown as ITipoProteccion;
    }
    return {
      id: value.id,
      nombre: value.nombre,
      descripcion: value.descripcion,
      tipoPropiedad: value.tipoPropiedad,
      padre: { id: value.padre?.id, nombre: value.padre?.nombre, descripcion: value.padre?.descripcion } as ITipoProteccion,
      activo: value.activo
    };
  }
  fromTarget(value: ITipoProteccion): ITipoProteccionResponse {
    if (!value) {
      return value as unknown as ITipoProteccionResponse;
    }
    return {
      id: value.id,
      nombre: value.nombre,
      descripcion: value.descripcion,
      tipoPropiedad: value.tipoPropiedad,
      padre: value.padre,
      activo: value.activo
    };
  }
}

export const TIPO_PROTECCION_RESPONSE_CONVERTER = new TipoProteccionResponseConverter();
