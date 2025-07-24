import { I18N_FIELD_RESPONSE_CONVERTER } from '@core/i18n/i18n-field.converter';
import { IEstadoSolicitud } from '@core/models/csp/estado-solicitud';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { SgiBaseConverter } from '@sgi/framework/core';
import { IEstadoSolicitudResponse } from './estado-solicitud-response';

class EstadoSolicitudResponseConverter extends SgiBaseConverter<IEstadoSolicitudResponse, IEstadoSolicitud> {

  toTarget(value: IEstadoSolicitudResponse): IEstadoSolicitud {
    if (!value) {
      return value as unknown as IEstadoSolicitud;
    }
    return {
      id: value.id,
      solicitudId: value.solicitudId,
      estado: value.estado,
      fechaEstado: LuxonUtils.fromBackend(value.fechaEstado),
      comentario: value.comentario ? I18N_FIELD_RESPONSE_CONVERTER.toTargetArray(value.comentario) : [],
    };
  }

  fromTarget(value: IEstadoSolicitud): IEstadoSolicitudResponse {
    if (!value) {
      return value as unknown as IEstadoSolicitudResponse;
    }
    return {
      id: value.id,
      solicitudId: value.solicitudId,
      estado: value.estado,
      fechaEstado: LuxonUtils.toBackend(value.fechaEstado),
      comentario: value.comentario ? I18N_FIELD_RESPONSE_CONVERTER.fromTargetArray(value.comentario) : [],
    };
  }
}

export const ESTADO_SOLICITUD_RESPONSE_CONVERTER = new EstadoSolicitudResponseConverter();
