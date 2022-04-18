import { ISolicitudHito } from '@core/models/csp/solicitud-hito';
import { ITipoHito } from '@core/models/csp/tipos-configuracion';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { SgiBaseConverter } from '@sgi/framework/core';
import { ISolicitudHitoRequest } from './solicitud-hito-request';

class SolicitudHitoRequestConverter extends SgiBaseConverter<ISolicitudHitoRequest, ISolicitudHito> {

  toTarget(value: ISolicitudHitoRequest): ISolicitudHito {
    if (!value) {
      return value as unknown as ISolicitudHito;
    }
    return {
      id: undefined,
      fecha: LuxonUtils.fromBackend(value.fecha),
      tipoHito: {
        id: value.tipoHitoId
      } as ITipoHito,
      comentario: value.comentario,
      solicitudId: value.solicitudId,
      aviso: value.aviso ? {
        email: {
          id: undefined,
          content: value.aviso.contenido,
          recipients: value.aviso.destinatarios.map(destinatario => ({ name: destinatario.nombre, address: destinatario.email })),
          subject: value.aviso.asunto
        },
        task: {
          id: undefined,
          instant: LuxonUtils.fromBackend(value.aviso.fechaEnvio)
        },
        incluirIpsSolicitud: value.aviso.incluirIpsSolicitud
      } : null
    };
  }

  fromTarget(value: ISolicitudHito): ISolicitudHitoRequest {
    if (!value) {
      return value as unknown as ISolicitudHitoRequest;
    }
    return {
      fecha: LuxonUtils.toBackend(value.fecha),
      tipoHitoId: value.tipoHito?.id,
      comentario: value.comentario,
      solicitudId: value.solicitudId,
      aviso: value.aviso ? {
        fechaEnvio: LuxonUtils.toBackend(value.aviso.task.instant),
        asunto: value.aviso.email.subject,
        contenido: value.aviso.email.content,
        destinatarios: value.aviso.email.recipients.map(recipient => ({ nombre: recipient.name, email: recipient.address })),
        incluirIpsSolicitud: value.aviso.incluirIpsSolicitud
      } : null
    };
  }
}

export const SOLICITUD_HITO_REQUEST_CONVERTER = new SolicitudHitoRequestConverter();
