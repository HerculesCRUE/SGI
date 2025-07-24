import { I18nFieldValueRequest } from "@core/i18n/i18n-field-request";

export interface IProcedimientoDocumentoRequest {

  nombre: I18nFieldValueRequest[];
  documentoRef: string;
  procedimientoId: number;

}