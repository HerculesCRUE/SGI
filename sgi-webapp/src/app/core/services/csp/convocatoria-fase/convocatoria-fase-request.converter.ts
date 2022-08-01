import { IConvocatoriaFase } from "@core/models/csp/convocatoria-fase";
import { IConvocatoriaFaseRequest } from "./convocatoria-fase-request";
import { SgiBaseConverter } from '@sgi/framework/core';
import { LuxonUtils } from "@core/utils/luxon-utils";
import { ITipoFase } from "@core/models/csp/tipos-configuracion";
import { IConvocatoriaFaseAvisoRequest } from "./convocatoria-fase-aviso-request";
import { IConvocatoriaFaseAviso } from "./convocatoria-fase-aviso";

class ConvocatoriaFaseRequestConverter extends SgiBaseConverter<IConvocatoriaFaseRequest, IConvocatoriaFase> {

  toTarget(value: IConvocatoriaFaseRequest): IConvocatoriaFase {
    return !!!value ? value as unknown as IConvocatoriaFase :
      {
        id: undefined,
        fechaInicio: LuxonUtils.fromBackend(value.fechaInicio),
        fechaFin: LuxonUtils.fromBackend(value.fechaFin),
        tipoFase: {
          id: value.tipoFaseId
        } as ITipoFase,
        observaciones: value.observaciones,
        convocatoriaId: value.convocatoriaId,
        aviso1: this.getConvocatoriaFaseAviso(value.aviso1),
        aviso2: this.getConvocatoriaFaseAviso(value.aviso2)
      };
  }

  fromTarget(value: IConvocatoriaFase): IConvocatoriaFaseRequest {
    return !!!value ? value as unknown as IConvocatoriaFaseRequest :
      {
        convocatoriaId: value.convocatoriaId,
        tipoFaseId: value.tipoFase?.id,
        fechaInicio: LuxonUtils.toBackend(value.fechaInicio),
        fechaFin: LuxonUtils.toBackend(value.fechaFin),
        observaciones: value.observaciones,
        aviso1: this.getConvocatoriaFaseAvisoRequest(value.aviso1),
        aviso2: this.getConvocatoriaFaseAvisoRequest(value.aviso2)
      }
  }

  private getConvocatoriaFaseAviso(aviso: IConvocatoriaFaseAvisoRequest): IConvocatoriaFaseAviso {
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
      incluirIpsSolicitud: aviso.incluirIpsSolicitud,
      incluirIpsProyecto: aviso.incluirIpsProyecto
    } : null;
  }

  private getConvocatoriaFaseAvisoRequest(aviso: IConvocatoriaFaseAviso): IConvocatoriaFaseAvisoRequest {
    return !!aviso ? {
      fechaEnvio: LuxonUtils.toBackend(aviso.task.instant),
      asunto: aviso.email.subject,
      contenido: aviso.email.content,
      destinatarios: aviso.email.recipients.map(recipient => ({ nombre: recipient.name, email: recipient.address })),
      incluirIpsSolicitud: aviso.incluirIpsSolicitud,
      incluirIpsProyecto: aviso.incluirIpsProyecto
    } : null;
  }
}
export const CONVOCATORIA_FASE_REQUEST_CONVERTER = new ConvocatoriaFaseRequestConverter();