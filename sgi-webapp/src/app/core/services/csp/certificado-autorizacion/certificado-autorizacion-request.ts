import { I18nFieldValueRequest } from "@core/i18n/i18n-field-request";

export interface ICertificadoAutorizacionRequest {
  autorizacionId: number;
  documentoRef: I18nFieldValueRequest[];
  nombre: I18nFieldValueRequest[];
  visible: boolean;
}
