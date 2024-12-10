import { IEstadoProyecto } from '@core/models/csp/estado-proyecto';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { SgiBaseConverter } from '@sgi/framework/core';
import { IEstadoProyectoRequest } from './estado-proyecto-request';

class EstadoProyectoRequestConverter extends SgiBaseConverter<IEstadoProyectoRequest, IEstadoProyecto> {

  toTarget(value: IEstadoProyectoRequest): IEstadoProyecto {
    if (!value) {
      return value as unknown as IEstadoProyecto;
    }
    return {
      id: undefined,
      proyectoId: value.proyectoId,
      estado: value.estado,
      fechaEstado: LuxonUtils.fromBackend(value.fechaEstado),
      comentario: value.comentario
    };
  }

  fromTarget(value: IEstadoProyecto): IEstadoProyectoRequest {
    if (!value) {
      return value as unknown as IEstadoProyectoRequest;
    }
    return {
      proyectoId: value.proyectoId,
      estado: value.estado,
      fechaEstado: LuxonUtils.toBackend(value.fechaEstado),
      comentario: value.comentario
    };
  }
}

export const ESTADO_PROYECTO_REQUEST_CONVERTER = new EstadoProyectoRequestConverter();
