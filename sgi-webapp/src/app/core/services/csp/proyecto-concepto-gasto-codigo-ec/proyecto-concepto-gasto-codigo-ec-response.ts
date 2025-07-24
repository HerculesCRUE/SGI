import { I18nFieldValueResponse } from "@core/i18n/i18n-field-response";

export interface IProyectoConceptoGastoCodigoEcResponse {
  id: number;
  proyectoConceptoGastoId: number;
  codigoEconomicoRef: string;
  fechaInicio: string;
  fechaFin: string;
  observaciones: I18nFieldValueResponse[];
  convocatoriaConceptoGastoCodigoEcId: number;
}
