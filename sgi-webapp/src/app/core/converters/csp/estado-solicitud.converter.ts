import { IEstadoSolicitudBackend } from '@core/models/csp/backend/estado-solicitud-backend';
import { IEstadoSolicitud } from '@core/models/csp/estado-solicitud';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { SgiBaseConverter } from '@sgi/framework/core';

class EstadoSolicitudConverter extends SgiBaseConverter<IEstadoSolicitudBackend, IEstadoSolicitud> {

  toTarget(value: IEstadoSolicitudBackend): IEstadoSolicitud {
    if (!value) {
      return value as unknown as IEstadoSolicitud;
    }
    return {
      id: value.id,
      solicitudId: value.solicitudId,
      estado: value.estado,
      fechaEstado: LuxonUtils.fromBackend(value.fechaEstado),
      comentario: value.comentario
    };
  }

  fromTarget(value: IEstadoSolicitud): IEstadoSolicitudBackend {
    if (!value) {
      return value as unknown as IEstadoSolicitudBackend;
    }
    return {
      id: value.id,
      solicitudId: value.solicitudId,
      estado: value.estado,
      fechaEstado: LuxonUtils.toBackend(value.fechaEstado),
      comentario: value.comentario
    };
  }
}

export const ESTADO_SOLICITUD_CONVERTER = new EstadoSolicitudConverter();
