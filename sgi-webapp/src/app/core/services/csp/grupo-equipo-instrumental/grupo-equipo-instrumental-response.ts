import { I18nFieldValueResponse } from "@core/i18n/i18n-field-response";

export interface IGrupoEquipoInstrumentalResponse {
  id: number;
  grupoId: number;
  nombre: I18nFieldValueResponse[];
  numRegistro: string;
  descripcion: I18nFieldValueResponse[];
}
