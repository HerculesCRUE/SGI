import { IEstadoProyectoResponse } from '@core/services/csp/estado-proyecto/estado-proyecto-response';
import { IEstadoProyecto } from '@core/models/csp/estado-proyecto';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { SgiBaseConverter } from '@sgi/framework/core';

class EstadoProyectoResponseConverter extends SgiBaseConverter<IEstadoProyectoResponse, IEstadoProyecto> {

  toTarget(value: IEstadoProyectoResponse): IEstadoProyecto {
    if (!value) {
      return value as unknown as IEstadoProyecto;
    }
    return {
      id: value.id,
      proyectoId: value.proyectoId,
      estado: value.estado,
      fechaEstado: LuxonUtils.fromBackend(value.fechaEstado),
      comentario: value.comentario
    };
  }

  fromTarget(value: IEstadoProyecto): IEstadoProyectoResponse {
    if (!value) {
      return value as unknown as IEstadoProyectoResponse;
    }
    return {
      id: value.id,
      proyectoId: value.proyectoId,
      estado: value.estado,
      fechaEstado: LuxonUtils.toBackend(value.fechaEstado),
      comentario: value.comentario
    };
  }
}

export const ESTADO_PROYECTO_RESPONSE_CONVERTER = new EstadoProyectoResponseConverter();
