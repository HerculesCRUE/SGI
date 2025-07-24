import { I18nFieldValueResponse } from "@core/i18n/i18n-field-response";

export interface ILineaInvestigacionResponse {
  id: number;
  nombre: I18nFieldValueResponse[];
  activo: boolean;
}
