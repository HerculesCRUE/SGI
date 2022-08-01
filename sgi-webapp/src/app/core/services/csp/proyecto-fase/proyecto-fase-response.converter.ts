import { IGenericEmailText } from "@core/models/com/generic-email-text";
import { IProyectoFase } from "@core/models/csp/proyecto-fase";
import { ITipoFase } from "@core/models/csp/tipos-configuracion";
import { ISendEmailTask } from "@core/models/tp/send-email-task";
import { LuxonUtils } from "@core/utils/luxon-utils";
import { SgiBaseConverter } from "@sgi/framework/core";
import { IProyectoFaseAviso } from "./proyecto-fase-aviso";
import { IProyectoFaseAvisoResponse } from "./proyecto-fase-aviso-response";
import { IProyectoFaseResponse } from "./proyecto-fase-response";

class ProyectoFaseResponseConverter extends SgiBaseConverter<IProyectoFaseResponse, IProyectoFase> {

  toTarget(value: IProyectoFaseResponse): IProyectoFase {
    return !!!value ? value as unknown as IProyectoFase :
      {
        id: value.id,
        fechaInicio: LuxonUtils.fromBackend(value.fechaInicio),
        fechaFin: LuxonUtils.fromBackend(value.fechaFin),
        tipoFase: value.tipoFase,
        observaciones: value.observaciones,
        proyectoId: value.proyectoId,
        aviso1: this.getConvocatoriaFaseAviso(value.aviso1),
        aviso2: this.getConvocatoriaFaseAviso(value.aviso2)
      };
  }
  fromTarget(value: IProyectoFase): IProyectoFaseResponse {
    return !!!value ? value as unknown as IProyectoFaseResponse :
      {
        id: value.id,
        fechaInicio: LuxonUtils.toBackend(value.fechaInicio),
        fechaFin: LuxonUtils.toBackend(value.fechaFin),
        tipoFase: {
          id: value.tipoFase.id
        } as ITipoFase,
        observaciones: value.observaciones,
        proyectoId: value.proyectoId,
        aviso1: this.getConvocatoriaFaseAvisoResponse(value.aviso1),
        aviso2: this.getConvocatoriaFaseAvisoResponse(value.aviso2)
      };
  }

  private getConvocatoriaFaseAviso(aviso: IProyectoFaseAvisoResponse): IProyectoFaseAviso {
    return aviso ? {
      email: {
        id: Number(aviso.comunicadoRef)
      } as IGenericEmailText,
      task: {
        id: Number(aviso.tareaProgramadaRef)
      } as ISendEmailTask,
      incluirIpsProyecto: aviso.incluirIpsProyecto
    } : null;
  }

  private getConvocatoriaFaseAvisoResponse(aviso: IProyectoFaseAviso): IProyectoFaseAvisoResponse {
    return !!aviso ? {
      comunicadoRef: aviso.email.id.toString(),
      tareaProgramadaRef: aviso.task.id.toString(),
      incluirIpsProyecto: aviso.incluirIpsProyecto
    } : null;
  }
}
export const PROYECTO_FASE_RESPONSE_CONVERTER = new ProyectoFaseResponseConverter();