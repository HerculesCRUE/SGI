import { I18N_FIELD_RESPONSE_CONVERTER } from "@core/i18n/i18n-field.converter";
import { IGenericEmailText } from "@core/models/com/generic-email-text";
import { IConvocatoriaFase } from "@core/models/csp/convocatoria-fase";
import { ISendEmailTask } from "@core/models/tp/send-email-task";
import { LuxonUtils } from "@core/utils/luxon-utils";
import { SgiBaseConverter } from "@sgi/framework/core";
import { TIPO_FASE_RESPONSE_CONVERTER } from "../tipo-fase/tipo-fase-response.converter";
import { IConvocatoriaFaseAviso } from "./convocatoria-fase-aviso";
import { IConvocatoriaFaseAvisoResponse } from "./convocatoria-fase-aviso-response";
import { IConvocatoriaFaseResponse } from "./convocatoria-fase-response";

class ConvocatoriaFaseResponseConverter extends SgiBaseConverter<IConvocatoriaFaseResponse, IConvocatoriaFase> {

  toTarget(value: IConvocatoriaFaseResponse): IConvocatoriaFase {
    return !!!value ? value as unknown as IConvocatoriaFase :
      {
        id: value.id,
        fechaInicio: LuxonUtils.fromBackend(value.fechaInicio),
        fechaFin: LuxonUtils.fromBackend(value.fechaFin),
        tipoFase: value.tipoFase ? TIPO_FASE_RESPONSE_CONVERTER.toTarget(value.tipoFase) : null,
        observaciones: value.observaciones ? I18N_FIELD_RESPONSE_CONVERTER.toTargetArray(value.observaciones) : [],
        convocatoriaId: value.convocatoriaId,
        aviso1: this.getConvocatoriaFaseAviso(value.aviso1),
        aviso2: this.getConvocatoriaFaseAviso(value.aviso2)
      };
  }
  fromTarget(value: IConvocatoriaFase): IConvocatoriaFaseResponse {
    return !!!value ? value as unknown as IConvocatoriaFaseResponse :
      {
        id: value.id,
        fechaInicio: LuxonUtils.toBackend(value.fechaInicio),
        fechaFin: LuxonUtils.toBackend(value.fechaFin),
        tipoFase: value.tipoFase ? TIPO_FASE_RESPONSE_CONVERTER.fromTarget(value.tipoFase) : null,
        observaciones: value.observaciones ? I18N_FIELD_RESPONSE_CONVERTER.fromTargetArray(value.observaciones) : [],
        convocatoriaId: value.convocatoriaId,
        aviso1: this.getConvocatoriaFaseAvisoResponse(value.aviso1),
        aviso2: this.getConvocatoriaFaseAvisoResponse(value.aviso2)
      };
  }

  private getConvocatoriaFaseAviso(aviso: IConvocatoriaFaseAvisoResponse): IConvocatoriaFaseAviso {
    return aviso ? {
      email: {
        id: Number(aviso.comunicadoRef)
      } as IGenericEmailText,
      task: {
        id: Number(aviso.tareaProgramadaRef)
      } as ISendEmailTask,
      incluirIpsSolicitud: aviso.incluirIpsSolicitud,
      incluirIpsProyecto: aviso.incluirIpsProyecto
    } : null;
  }

  private getConvocatoriaFaseAvisoResponse(aviso: IConvocatoriaFaseAviso): IConvocatoriaFaseAvisoResponse {
    return !!aviso ? {
      comunicadoRef: aviso.email.id.toString(),
      tareaProgramadaRef: aviso.task.id.toString(),
      incluirIpsSolicitud: aviso.incluirIpsSolicitud,
      incluirIpsProyecto: aviso.incluirIpsProyecto
    } : null;
  }
}
export const CONVOCATORIA_FASE_RESPONSE_CONVERTER = new ConvocatoriaFaseResponseConverter();