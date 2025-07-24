import { I18nFieldValueResponse } from "@core/i18n/i18n-field-response";

export interface ITipoFacturacionResponse {
  id: number;
  nombre: I18nFieldValueResponse[];
  incluirEnComunicado: boolean;
  activo: boolean;
}
