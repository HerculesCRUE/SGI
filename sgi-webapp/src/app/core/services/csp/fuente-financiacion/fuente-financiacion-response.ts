import { I18nFieldValueResponse } from "@core/i18n/i18n-field-response";

export interface IFuenteFinanciacionResponse {
  id: number;
  nombre: I18nFieldValueResponse[];
  descripcion: I18nFieldValueResponse[];
  fondoEstructural: boolean;
  tipoAmbitoGeografico: {
    id: number;
    nombre: I18nFieldValueResponse[];
  };
  tipoOrigenFuenteFinanciacion: {
    id: number;
    nombre: I18nFieldValueResponse[];
  };
  activo: boolean;
}
