import { IUnidadGestion } from '@core/models/usr/unidad-gestion';
import { SgiBaseConverter } from '@sgi/framework/core';
import { IUnidadGestionRequest } from './unidad-gestion-request';

class UnidadGestionRequestConverter
  extends SgiBaseConverter<IUnidadGestionRequest, IUnidadGestion> {
  toTarget(value: IUnidadGestionRequest): IUnidadGestion {
    if (!value) {
      return value as unknown as IUnidadGestion;
    }
    return {
      id: value.id,
      acronimo: value.acronimo,
      activo: true,
      descripcion: value.descripcion,
      nombre: value.nombre
    };
  }

  fromTarget(value: IUnidadGestion): IUnidadGestionRequest {
    if (!value) {
      return value as unknown as IUnidadGestionRequest;
    }
    return {
      id: value.id,
      acronimo: value.acronimo,
      descripcion: value.descripcion,
      nombre: value.nombre
    };
  }
}

export const UNIDAD_GESTION_REQUEST_CONVERTER = new UnidadGestionRequestConverter();
