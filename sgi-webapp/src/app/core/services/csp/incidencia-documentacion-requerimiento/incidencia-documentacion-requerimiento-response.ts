import { I18nFieldValueResponse } from "@core/i18n/i18n-field-response";

export interface IIncidenciaDocumentacionRequerimientoResponse {
  id: number;
  requerimientoJustificacionId: number;
  nombreDocumento: I18nFieldValueResponse[];
  incidencia: I18nFieldValueResponse[];
  alegacion: I18nFieldValueResponse[];
}
