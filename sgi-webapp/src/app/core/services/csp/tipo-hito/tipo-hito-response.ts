import { I18nFieldValueResponse } from "@core/i18n/i18n-field-response";

export interface ITipoHitoResponse {
  id: number;
  nombre: I18nFieldValueResponse[];
  descripcion: I18nFieldValueResponse[];
  activo: boolean;
}
