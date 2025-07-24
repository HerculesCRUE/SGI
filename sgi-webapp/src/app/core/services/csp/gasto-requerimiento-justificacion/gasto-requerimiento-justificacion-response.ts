import { I18nFieldValueResponse } from "@core/i18n/i18n-field-response";

export interface IGastoRequerimientoJustificacionResponse {
  id: number;
  gastoRef: string;
  importeAceptado: number;
  importeRechazado: number;
  importeAlegado: number;
  aceptado: boolean;
  incidencia: I18nFieldValueResponse[];
  alegacion: I18nFieldValueResponse[];
  identificadorJustificacion: string;
  requerimientoJustificacionId: number;
}
