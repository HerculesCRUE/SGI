import { IProyectoAgrupacionGasto } from '@core/models/csp/proyecto-agrupacion-gasto';
import { SgiBaseConverter } from '@sgi/framework/core';
import { IProyectoAgrupacionGastoRequest } from './proyecto-agrupacion-gasto-request';

class ProyectoAgrupacionGastoRequestConverter
  extends SgiBaseConverter<IProyectoAgrupacionGastoRequest, IProyectoAgrupacionGasto> {
  toTarget(value: IProyectoAgrupacionGastoRequest): IProyectoAgrupacionGasto {
    if (!value) {
      return value as unknown as IProyectoAgrupacionGasto;
    }
    return {
      id: undefined,
      proyectoId: value.proyectoId,
      nombre: value.nombre
    };
  }

  fromTarget(value: IProyectoAgrupacionGasto): IProyectoAgrupacionGastoRequest {
    if (!value) {
      return value as unknown as IProyectoAgrupacionGastoRequest;
    }
    return {
      proyectoId: value.proyectoId,
      nombre: value.nombre
    };
  }
}

export const PROYECTO_AGRUPACION_GASTO_REQUEST_CONVERTER = new ProyectoAgrupacionGastoRequestConverter();
