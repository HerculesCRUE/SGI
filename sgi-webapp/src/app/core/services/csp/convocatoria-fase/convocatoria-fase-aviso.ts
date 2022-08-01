import { IGenericEmailText } from "@core/models/com/generic-email-text";
import { ISendEmailTask } from "@core/models/tp/send-email-task";

export interface IConvocatoriaFaseAviso {
  email: IGenericEmailText;
  task: ISendEmailTask;
  incluirIpsSolicitud: boolean;
  incluirIpsProyecto: boolean;
}