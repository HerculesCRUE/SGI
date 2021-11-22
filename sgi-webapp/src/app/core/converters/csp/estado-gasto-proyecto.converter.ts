import { IEstadoGastoProyectoBackend } from '@core/models/csp/backend/estado-gasto-proyecto-backend';
import { IEstadoGastoProyecto } from '@core/models/csp/estado-gasto-proyecto';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { SgiBaseConverter } from '@sgi/framework/core';

class EstadoGastoProyectoConverter extends SgiBaseConverter<IEstadoGastoProyectoBackend, IEstadoGastoProyecto> {

  toTarget(value: IEstadoGastoProyectoBackend): IEstadoGastoProyecto {
    if (!value) {
      return value as unknown as IEstadoGastoProyecto;
    }
    return {
      id: value.id,
      gastoProyectoId: value.gastoProyectoId,
      estado: value.estado,
      fechaEstado: LuxonUtils.fromBackend(value.fechaEstado),
      comentario: value.comentario
    };
  }

  fromTarget(value: IEstadoGastoProyecto): IEstadoGastoProyectoBackend {
    if (!value) {
      return value as unknown as IEstadoGastoProyectoBackend;
    }
    return {
      id: value.id,
      gastoProyectoId: value.gastoProyectoId,
      estado: value.estado,
      fechaEstado: LuxonUtils.toBackend(value.fechaEstado),
      comentario: value.comentario
    };
  }
}

export const ESTADO_GASTO_PROYECTO_CONVERTER = new EstadoGastoProyectoConverter();
