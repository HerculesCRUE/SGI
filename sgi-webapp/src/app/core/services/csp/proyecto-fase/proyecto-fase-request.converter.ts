import { IProyectoFase } from "@core/models/csp/proyecto-fase";
import { IProyectoFaseRequest } from "./proyecto-fase-request";
import { SgiBaseConverter } from '@sgi/framework/core';
import { LuxonUtils } from "@core/utils/luxon-utils";
import { ITipoFase } from "@core/models/csp/tipos-configuracion";
import { IProyectoFaseAvisoRequest } from "./proyecto-fase-aviso-request";
import { IProyectoFaseAviso } from "./proyecto-fase-aviso";

class ProyectoFaseRequestConverter extends SgiBaseConverter<IProyectoFaseRequest, IProyectoFase> {

  toTarget(value: IProyectoFaseRequest): IProyectoFase {
    return !!!value ? value as unknown as IProyectoFase :
      {
        id: undefined,
        fechaInicio: LuxonUtils.fromBackend(value.fechaInicio),
        fechaFin: LuxonUtils.fromBackend(value.fechaFin),
        tipoFase: {
          id: value.tipoFaseId
        } as ITipoFase,
        observaciones: value.observaciones,
        proyectoId: value.proyectoId,
        aviso1: this.getConvocatoriaFaseAviso(value.aviso1),
        aviso2: this.getConvocatoriaFaseAviso(value.aviso2)
      };
  }

  fromTarget(value: IProyectoFase): IProyectoFaseRequest {
    return !!!value ? value as unknown as IProyectoFaseRequest :
      {
        proyectoId: value.proyectoId,
        tipoFaseId: value.tipoFase?.id,
        fechaInicio: LuxonUtils.toBackend(value.fechaInicio),
        fechaFin: LuxonUtils.toBackend(value.fechaFin),
        observaciones: value.observaciones,
        aviso1: this.getConvocatoriaFaseAvisoRequest(value.aviso1),
        aviso2: this.getConvocatoriaFaseAvisoRequest(value.aviso2)
      }
  }

  private getConvocatoriaFaseAviso(aviso: IProyectoFaseAvisoRequest): IProyectoFaseAviso {
    return aviso ? {
      email: {
        id: undefined,
        content: aviso.contenido,
        recipients: aviso.destinatarios.map(destinatario => ({ name: destinatario.nombre, address: destinatario.email })),
        subject: aviso.asunto
      },
      task: {
        id: undefined,
        instant: LuxonUtils.fromBackend(aviso.fechaEnvio)
      },
      incluirIpsProyecto: aviso.incluirIpsProyecto
    } : null;
  }

  private getConvocatoriaFaseAvisoRequest(aviso: IProyectoFaseAviso): IProyectoFaseAvisoRequest {
    return !!aviso ? {
      fechaEnvio: LuxonUtils.toBackend(aviso.task.instant),
      asunto: aviso.email.subject,
      contenido: aviso.email.content,
      destinatarios: aviso.email.recipients.map(recipient => ({ nombre: recipient.name, email: recipient.address })),
      incluirIpsProyecto: aviso.incluirIpsProyecto
    } : null;
  }
}
export const PROYECTO_FASE_REQUEST_CONVERTER = new ProyectoFaseRequestConverter();