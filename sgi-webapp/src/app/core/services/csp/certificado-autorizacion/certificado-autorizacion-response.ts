import { I18nFieldValueResponse } from "@core/i18n/i18n-field-response";

export interface ICertificadoAutorizacionResponse {
  id: number;
  autorizacionId: number;
  documentoRef: I18nFieldValueResponse[];
  nombre: I18nFieldValueResponse[];
  visible: boolean;
}
