import { I18nFieldValueResponse } from "@core/i18n/i18n-field-response";

export interface IModeloEjecucionResponse {
  id: number;
  nombre: I18nFieldValueResponse[];
  descripcion: I18nFieldValueResponse[];
  externo: boolean;
  contrato: boolean;
  solicitudSinConvocatoria: boolean;
  activo: boolean;
}
