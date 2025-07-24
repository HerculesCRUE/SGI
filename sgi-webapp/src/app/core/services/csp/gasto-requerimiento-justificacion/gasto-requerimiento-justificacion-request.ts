import { I18nFieldValueRequest } from "@core/i18n/i18n-field-request";

export interface IGastoRequerimientoJustificacionRequest {
  gastoRef: string;
  requerimientoJustificacionId: number;
  importeAceptado: number;
  importeRechazado: number;
  importeAlegado: number;
  aceptado: boolean;
  incidencia: I18nFieldValueRequest[];
  alegacion: I18nFieldValueRequest[];
  identificadorJustificacion: string;
}
