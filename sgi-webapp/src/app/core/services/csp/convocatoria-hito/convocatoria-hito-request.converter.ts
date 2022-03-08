import { IConvocatoriaHito } from '@core/models/csp/convocatoria-hito';
import { ITipoHito } from '@core/models/csp/tipos-configuracion';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { SgiBaseConverter } from '@sgi/framework/core';
import { IConvocatoriaHitoRequest } from './convocatoria-hito-request';

class ConvocatoriaHitoRequestConverter extends SgiBaseConverter<IConvocatoriaHitoRequest, IConvocatoriaHito> {

  toTarget(value: IConvocatoriaHitoRequest): IConvocatoriaHito {
    if (!value) {
      return value as unknown as IConvocatoriaHito;
    }
    return {
      id: undefined,
      fecha: LuxonUtils.fromBackend(value.fecha),
      tipoHito: {
        id: value.tipoHitoId
      } as ITipoHito,
      comentario: value.comentario,
      convocatoriaId: value.convocatoriaId,
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
        incluirIpsSolicitud: value.aviso.incluirIpsSolicitud,
        incluirIpsProyecto: value.aviso.incluirIpsProyecto
      } : null
    };
  }

  fromTarget(value: IConvocatoriaHito): IConvocatoriaHitoRequest {
    if (!value) {
      return value as unknown as IConvocatoriaHitoRequest;
    }
    return {
      fecha: LuxonUtils.toBackend(value.fecha),
      tipoHitoId: value.tipoHito?.id,
      comentario: value.comentario,
      convocatoriaId: value.convocatoriaId,
      aviso: value.aviso ? {
        fechaEnvio: LuxonUtils.toBackend(value.aviso.task.instant),
        asunto: value.aviso.email.subject,
        contenido: value.aviso.email.content,
        destinatarios: value.aviso.email.recipients.map(recipient => ({ nombre: recipient.name, email: recipient.address })),
        incluirIpsSolicitud: value.aviso.incluirIpsSolicitud,
        incluirIpsProyecto: value.aviso.incluirIpsProyecto
      } : null
    };
  }
}

export const CONVOCATORIA_HITO_REQUEST_CONVERTER = new ConvocatoriaHitoRequestConverter();
