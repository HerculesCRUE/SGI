import { I18N_FIELD_REQUEST_CONVERTER } from '@core/i18n/i18n-field.converter';
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
      comentario: value.comentario ? I18N_FIELD_REQUEST_CONVERTER.toTargetArray(value.comentario) : []
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
      comentario: value.comentario ? I18N_FIELD_REQUEST_CONVERTER.fromTargetArray(value.comentario) : []
    };
  }
}

export const ESTADO_PROYECTO_REQUEST_CONVERTER = new EstadoProyectoRequestConverter();
