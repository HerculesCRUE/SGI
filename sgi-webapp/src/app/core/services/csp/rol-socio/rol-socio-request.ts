import { I18nFieldValueRequest } from "@core/i18n/i18n-field-request";

export interface IRolSocioRequest {
  nombre: I18nFieldValueRequest[];
  abreviatura: I18nFieldValueRequest[];
  coordinador: boolean;
  descripcion: I18nFieldValueRequest[];
}

