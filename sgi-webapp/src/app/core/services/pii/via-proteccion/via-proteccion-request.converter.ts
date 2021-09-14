import { IViaProteccion } from '@core/models/pii/via-proteccion';
import { SgiBaseConverter } from '@sgi/framework/core';
import { IViaProteccionRequest } from './via-proteccion-request';

class ViaProteccionRequestConverter extends SgiBaseConverter<IViaProteccionRequest, IViaProteccion>{

  toTarget(value: IViaProteccionRequest): IViaProteccion {
    if (!value) {
      return value as unknown as IViaProteccion;
    }
    return {
      id: undefined,
      nombre: value.nombre,
      descripcion: value.descripcion,
      tipoPropiedad: value.tipoPropiedad,
      mesesPrioridad: value.mesesPrioridad,
      paisEspecifico: value.paisEspecifico,
      extensionInternacional: value.extensionInternacional,
      variosPaises: value.variosPaises,
      activo: true
    };
  }
  fromTarget(value: IViaProteccion): IViaProteccionRequest {
    if (!value) {
      return value as unknown as IViaProteccionRequest;
    }
    return {
      nombre: value.nombre,
      descripcion: value.descripcion,
      tipoPropiedad: value.tipoPropiedad,
      mesesPrioridad: value.mesesPrioridad,
      paisEspecifico: value.paisEspecifico,
      extensionInternacional: value.extensionInternacional,
      variosPaises: value.variosPaises
    };
  }
}

export const VIA_PROTECCION_REQUEST_CONVERTER = new ViaProteccionRequestConverter();
