import { I18nFieldValueResponse } from "@core/i18n/i18n-field-response";

export interface IProgramaResponse {
  id: number;
  nombre: I18nFieldValueResponse[];
  descripcion: I18nFieldValueResponse[];
  padre: IProgramaResponse;
  activo: boolean;
}
