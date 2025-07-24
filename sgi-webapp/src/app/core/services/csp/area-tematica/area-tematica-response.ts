import { I18nFieldValueResponse } from "@core/i18n/i18n-field-response";

export interface IAreaTematicaResponse {
  id: number;
  nombre: I18nFieldValueResponse[];
  descripcion: I18nFieldValueResponse[];
  padre: IAreaTematicaResponse;
  activo: boolean;
}

