import { ISectorAplicacion } from '@core/models/pii/sector-aplicacion';
import { SgiBaseConverter } from '@sgi/framework/core';
import { ISectorAplicacionRequest } from './sector-aplicacion-request';

class SectorAplicacionRequestConverter extends SgiBaseConverter<ISectorAplicacionRequest, ISectorAplicacion>{
  toTarget(value: ISectorAplicacionRequest): ISectorAplicacion {
    if (!value) {
      return value as unknown as ISectorAplicacion;
    }
    return {
      id: undefined,
      nombre: value.nombre,
      descripcion: value.descripcion,
      activo: true
    };
  }
  fromTarget(value: ISectorAplicacion): ISectorAplicacionRequest {
    if (!value) {
      return value as unknown as ISectorAplicacionRequest;
    }
    return {
      nombre: value.nombre,
      descripcion: value.descripcion,
    };
  }
}

export const SECTOR_APLICACION_REQUEST_CONVERTER = new SectorAplicacionRequestConverter();
