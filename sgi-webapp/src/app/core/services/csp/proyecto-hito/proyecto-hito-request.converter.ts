import { IProyectoHito } from '@core/models/csp/proyecto-hito';
import { ITipoHito } from '@core/models/csp/tipos-configuracion';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { SgiBaseConverter } from '@sgi/framework/core';
import { IProyectoHitoRequest } from './proyecto-hito-request';

class ProyectoHitoRequestConverter extends SgiBaseConverter<IProyectoHitoRequest, IProyectoHito> {

  toTarget(value: IProyectoHitoRequest): IProyectoHito {
    if (!value) {
      return value as unknown as IProyectoHito;
    }
    return {
      id: undefined,
      fecha: LuxonUtils.fromBackend(value.fecha),
      tipoHito: {
        id: value.tipoHitoId
      } as ITipoHito,
      comentario: value.comentario,
      proyectoId: value.proyectoId,
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
        incluirIpsProyecto: value.aviso.incluirIpsProyecto
      } : null
    };
  }

  fromTarget(value: IProyectoHito): IProyectoHitoRequest {
    if (!value) {
      return value as unknown as IProyectoHitoRequest;
    }
    return {
      fecha: LuxonUtils.toBackend(value.fecha),
      tipoHitoId: value.tipoHito?.id,
      comentario: value.comentario,
      proyectoId: value.proyectoId,
      aviso: value.aviso ? {
        fechaEnvio: LuxonUtils.toBackend(value.aviso.task.instant),
        asunto: value.aviso.email.subject,
        contenido: value.aviso.email.content,
        destinatarios: value.aviso.email.recipients.map(recipient => ({ nombre: recipient.name, email: recipient.address })),
        incluirIpsProyecto: value.aviso.incluirIpsProyecto
      } : null
    };
  }
}

export const PROYECTO_HITO_REQUEST_CONVERTER = new ProyectoHitoRequestConverter();
