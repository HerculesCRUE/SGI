import { I18nFieldValueRequest } from "@core/i18n/i18n-field-request";

export interface IInvencionDocumentoRequest {

  nombre: I18nFieldValueRequest[];
  documentoRef: string;
  fechaAnadido: string;
  invencionId: number;
}
