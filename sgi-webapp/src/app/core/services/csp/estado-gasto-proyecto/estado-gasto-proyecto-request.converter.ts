import { I18N_FIELD_REQUEST_CONVERTER } from '@core/i18n/i18n-field.converter';
import { IEstadoGastoProyecto } from '@core/models/csp/estado-gasto-proyecto';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { SgiBaseConverter } from '@sgi/framework/core';
import { IEstadoGastoProyectoRequest } from './estado-gasto-proyecto-request';

class EstadoGastoProyectoRequestConverter extends SgiBaseConverter<IEstadoGastoProyectoRequest, IEstadoGastoProyecto> {

  toTarget(value: IEstadoGastoProyectoRequest): IEstadoGastoProyecto {
    if (!value) {
      return value as unknown as IEstadoGastoProyecto;
    }
    return {
      id: value.id,
      gastoProyectoId: value.gastoProyectoId,
      estado: value.estado,
      fechaEstado: LuxonUtils.fromBackend(value.fechaEstado),
      comentario: value.comentario ? I18N_FIELD_REQUEST_CONVERTER.toTargetArray(value.comentario) : []
    };
  }

  fromTarget(value: IEstadoGastoProyecto): IEstadoGastoProyectoRequest {
    if (!value) {
      return value as unknown as IEstadoGastoProyectoRequest;
    }
    return {
      id: value.id,
      gastoProyectoId: value.gastoProyectoId,
      estado: value.estado,
      fechaEstado: LuxonUtils.toBackend(value.fechaEstado),
      comentario: value.comentario ? I18N_FIELD_REQUEST_CONVERTER.fromTargetArray(value.comentario) : []
    };
  }
}

export const ESTADO_GASTO_PROYECTO_REQUEST_CONVERTER = new EstadoGastoProyectoRequestConverter();
