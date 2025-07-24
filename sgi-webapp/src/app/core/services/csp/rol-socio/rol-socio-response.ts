import { I18nFieldValueResponse } from "@core/i18n/i18n-field-response";

export interface IRolSocioResponse {
  id: number;
  nombre: I18nFieldValueResponse[];
  abreviatura: I18nFieldValueResponse[];
  coordinador: boolean;
  descripcion: I18nFieldValueResponse[];
  activo: boolean;
}
