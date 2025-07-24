import { I18N_FIELD_RESPONSE_CONVERTER } from '@core/i18n/i18n-field.converter';
import { IEstadoGastoProyecto } from '@core/models/csp/estado-gasto-proyecto';
import { IEstadoGastoProyectoResponse } from '@core/services/csp/estado-gasto-proyecto/estado-gasto-proyecto-response';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { SgiBaseConverter } from '@sgi/framework/core';

class EstadoGastoProyectoResponseConverter extends SgiBaseConverter<IEstadoGastoProyectoResponse, IEstadoGastoProyecto> {

  toTarget(value: IEstadoGastoProyectoResponse): IEstadoGastoProyecto {
    if (!value) {
      return value as unknown as IEstadoGastoProyecto;
    }
    return {
      id: value.id,
      gastoProyectoId: value.gastoProyectoId,
      estado: value.estado,
      fechaEstado: LuxonUtils.fromBackend(value.fechaEstado),
      comentario: value.comentario ? I18N_FIELD_RESPONSE_CONVERTER.toTargetArray(value.comentario) : []
    };
  }

  fromTarget(value: IEstadoGastoProyecto): IEstadoGastoProyectoResponse {
    if (!value) {
      return value as unknown as IEstadoGastoProyectoResponse;
    }
    return {
      id: value.id,
      gastoProyectoId: value.gastoProyectoId,
      estado: value.estado,
      fechaEstado: LuxonUtils.toBackend(value.fechaEstado),
      comentario: value.comentario ? I18N_FIELD_RESPONSE_CONVERTER.fromTargetArray(value.comentario) : []
    };
  }
}

export const ESTADO_GASTO_PROYECTO_RESPONSE_CONVERTER = new EstadoGastoProyectoResponseConverter();
