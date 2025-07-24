import { I18nFieldValueRequest } from "@core/i18n/i18n-field-request";

export interface IIncidenciaDocumentacionRequerimientoRequest {
  requerimientoJustificacionId: number;
  nombreDocumento: I18nFieldValueRequest[];
  incidencia: I18nFieldValueRequest[];
}
