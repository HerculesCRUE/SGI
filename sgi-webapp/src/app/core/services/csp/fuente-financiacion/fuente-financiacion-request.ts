import { I18nFieldValueRequest } from "@core/i18n/i18n-field-request";

export interface IFuenteFinanciacionRequest {
  nombre: I18nFieldValueRequest[];
  descripcion: I18nFieldValueRequest[];
  fondoEstructural: boolean;
  tipoAmbitoGeograficoId: number;
  tipoOrigenFuenteFinanciacionId: number;
}
