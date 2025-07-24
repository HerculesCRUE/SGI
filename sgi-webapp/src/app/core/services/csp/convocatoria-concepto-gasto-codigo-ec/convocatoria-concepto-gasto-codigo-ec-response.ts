import { I18nFieldValueResponse } from "@core/i18n/i18n-field-response";

export interface IConvocatoriaConceptoGastoCodigoEcResponse {
  /** id */
  id: number;
  /** Id de ConvocatoriaConceptoGasto */
  convocatoriaConceptoGastoId: number;
  /** Referencia código económico */
  codigoEconomicoRef: string;
  /** Fecha inicio */
  fechaInicio: string;
  /** Fecha fin */
  fechaFin: string;
  /** Observaciones */
  observaciones: I18nFieldValueResponse[];
}
