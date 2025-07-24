import { I18nFieldValueRequest } from "@core/i18n/i18n-field-request";
import { IProyectoFaseAvisoRequest } from "./proyecto-fase-aviso-request";

export interface IProyectoFaseRequest {
  proyectoId: number;
  tipoFaseId: number;
  fechaInicio: string;
  fechaFin: string;
  observaciones: I18nFieldValueRequest[];
  aviso1: IProyectoFaseAvisoRequest;
  aviso2: IProyectoFaseAvisoRequest;
}
