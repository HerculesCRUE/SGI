import { I18nFieldValueRequest } from "@core/i18n/i18n-field-request";

export interface IEmpresaDocumentoRequest {
  nombre: I18nFieldValueRequest[];
  documentoRef: string;
  comentarios: I18nFieldValueRequest[];
  empresaId: number;
  tipoDocumentoId: number;
}
