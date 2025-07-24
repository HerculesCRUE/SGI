import { I18nFieldValueResponse } from "@core/i18n/i18n-field-response";
import { IConceptoGastoResponse } from "../concepto-gasto/concepto-gasto-response";

export interface IProyectoConceptoGastoResponse {
  id: number;
  proyectoId: number;
  conceptoGasto: IConceptoGastoResponse;
  permitido: boolean;
  importeMaximo: number;
  fechaInicio: string;
  fechaFin: string;
  observaciones: I18nFieldValueResponse[];
  convocatoriaConceptoGastoId: number;
}
