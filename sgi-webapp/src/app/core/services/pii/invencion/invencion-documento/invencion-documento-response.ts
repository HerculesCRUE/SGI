import { I18nFieldValueResponse } from "@core/i18n/i18n-field-response";

export interface IInvencionDocumentoResponse {
  id: number;
  fechaAnadido: string;
  nombre: I18nFieldValueResponse[];
  documentoRef: string;
  invencionId: number;
}
