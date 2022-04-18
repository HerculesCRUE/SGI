import { IGenericEmailText } from '@core/models/com/generic-email-text';
import { ISolicitudHito } from '@core/models/csp/solicitud-hito';
import { ITipoHito } from '@core/models/csp/tipos-configuracion';
import { ISendEmailTask } from '@core/models/tp/send-email-task';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { SgiBaseConverter } from '@sgi/framework/core';
import { ISolicitudHitoResponse } from './solicitud-hito-response';

class SolicitudHitoResponseConverter extends SgiBaseConverter<ISolicitudHitoResponse, ISolicitudHito> {

  toTarget(value: ISolicitudHitoResponse): ISolicitudHito {
    if (!value) {
      return value as unknown as ISolicitudHito;
    }
    return {
      id: value.id,
      fecha: LuxonUtils.fromBackend(value.fecha),
      tipoHito: value.tipoHito,
      comentario: value.comentario,
      solicitudId: value.solicitudId,
      aviso: value.aviso ? {
        email: {
          id: Number(value.aviso.comunicadoRef)
        } as IGenericEmailText,
        task: {
          id: Number(value.aviso.tareaProgramadaRef)
        } as ISendEmailTask,
        incluirIpsSolicitud: value.aviso.incluirIpsSolicitud,
      } : null
    };
  }

  fromTarget(value: ISolicitudHito): ISolicitudHitoResponse {
    if (!value) {
      return value as unknown as ISolicitudHitoResponse;
    }
    return {
      id: value.id,
      fecha: LuxonUtils.toBackend(value.fecha),
      tipoHito: {
        id: value.tipoHito.id
      } as ITipoHito,
      comentario: value.comentario,
      solicitudId: value.solicitudId,
      aviso: value.aviso ? {
        comunicadoRef: value.aviso.email.id.toString(),
        tareaProgramadaRef: value.aviso.task.id.toString(),
        incluirIpsSolicitud: value.aviso.incluirIpsSolicitud,
      } : null
    };
  }
}

export const SOLICITUD_HITO_RESPONSE_CONVERTER = new SolicitudHitoResponseConverter();
