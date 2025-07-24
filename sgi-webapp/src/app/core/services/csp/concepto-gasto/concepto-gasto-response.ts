import { I18nFieldValueResponse } from "@core/i18n/i18n-field-response";

export interface IConceptoGastoResponse {
  id: number;
  nombre: I18nFieldValueResponse[];
  descripcion: I18nFieldValueResponse[];
  costesIndirectos: boolean;
  activo: boolean;
}
