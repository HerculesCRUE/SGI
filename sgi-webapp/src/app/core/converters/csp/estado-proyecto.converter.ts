import { IEstadoProyectoBackend } from '@core/models/csp/backend/estado-proyecto-backend';
import { IEstadoProyecto } from '@core/models/csp/estado-proyecto';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { SgiBaseConverter } from '@sgi/framework/core';

class EstadoProyectoConverter extends SgiBaseConverter<IEstadoProyectoBackend, IEstadoProyecto> {

  toTarget(value: IEstadoProyectoBackend): IEstadoProyecto {
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

  fromTarget(value: IEstadoProyecto): IEstadoProyectoBackend {
    if (!value) {
      return value as unknown as IEstadoProyectoBackend;
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

export const ESTADO_PROYECTO_CONVERTER = new EstadoProyectoConverter();
