import { IProyectoAgrupacionGasto } from '@core/models/csp/proyecto-agrupacion-gasto';
import { SgiBaseConverter } from '@sgi/framework/core';
import { IProyectoAgrupacionGastoResponse } from './proyecto-agrupacion-gasto-response';

class ProyectoAgrupacionGastoResponseConverter
  extends SgiBaseConverter<IProyectoAgrupacionGastoResponse, IProyectoAgrupacionGasto> {
  toTarget(value: IProyectoAgrupacionGastoResponse): IProyectoAgrupacionGasto {
    if (!value) {
      return value as unknown as IProyectoAgrupacionGasto;
    }
    return {
      id: value.id,
      proyectoId: value.proyectoId,
      nombre: value.nombre
    };
  }

  fromTarget(value: IProyectoAgrupacionGasto): IProyectoAgrupacionGastoResponse {
    if (!value) {
      return value as unknown as IProyectoAgrupacionGastoResponse;
    }
    return {
      id: value.id,
      proyectoId: value.proyectoId,
      nombre: value.nombre
    };
  }
}

export const PROYECTO_AGRUPACION_GASTO_RESPONSE_CONVERTER = new ProyectoAgrupacionGastoResponseConverter();
