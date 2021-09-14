import { IViaProteccion } from '@core/models/pii/via-proteccion';
import { SgiBaseConverter } from '@sgi/framework/core';
import { IViaProteccionResponse } from './via-proteccion-response';

class ViaProteccionResponseConverter extends SgiBaseConverter<IViaProteccionResponse, IViaProteccion>{

  toTarget(value: IViaProteccionResponse): IViaProteccion {
    if (!value) {
      return value as unknown as IViaProteccion;
    }
    return {
      id: value.id,
      nombre: value.nombre,
      descripcion: value.descripcion,
      tipoPropiedad: value.tipoPropiedad,
      mesesPrioridad: value.mesesPrioridad,
      paisEspecifico: value.paisEspecifico,
      extensionInternacional: value.extensionInternacional,
      variosPaises: value.variosPaises,
      activo: value.activo
    };
  }
  fromTarget(value: IViaProteccion): IViaProteccionResponse {
    if (!value) {
      return value as unknown as IViaProteccionResponse;
    }
    return {
      id: value.id,
      nombre: value.nombre,
      descripcion: value.descripcion,
      tipoPropiedad: value.tipoPropiedad,
      mesesPrioridad: value.mesesPrioridad,
      paisEspecifico: value.paisEspecifico,
      extensionInternacional: value.extensionInternacional,
      variosPaises: value.variosPaises,
      activo: value.activo
    };
  }
}

export const VIA_PROTECCION_RESPONSE_CONVERTER = new ViaProteccionResponseConverter();
