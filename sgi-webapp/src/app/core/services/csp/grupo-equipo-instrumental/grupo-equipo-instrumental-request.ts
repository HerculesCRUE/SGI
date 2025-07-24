import { I18nFieldValueRequest } from "@core/i18n/i18n-field-request";

export interface IGrupoEquipoInstrumentalRequest {
  id: number;
  grupoId: number;
  nombre: I18nFieldValueRequest[];
  numRegistro: string;
  descripcion: I18nFieldValueRequest[];
}
