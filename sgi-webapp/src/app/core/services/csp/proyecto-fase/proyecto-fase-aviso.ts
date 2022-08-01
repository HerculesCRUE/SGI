import { IGenericEmailText } from "@core/models/com/generic-email-text";
import { ISendEmailTask } from "@core/models/tp/send-email-task";

export interface IProyectoFaseAviso {
  email: IGenericEmailText;
  task: ISendEmailTask;
  incluirIpsProyecto: boolean;
}