import { I18nFieldValueRequest } from "@core/i18n/i18n-field-request";
import { IConvocatoriaFaseAvisoRequest } from "./convocatoria-fase-aviso-request";

export interface IConvocatoriaFaseRequest {
  convocatoriaId: number;
  tipoFaseId: number;
  fechaInicio: string;
  fechaFin: string;
  observaciones: I18nFieldValueRequest[];
  aviso1: IConvocatoriaFaseAvisoRequest;
  aviso2: IConvocatoriaFaseAvisoRequest;
}
