import { I18nFieldValueResponse } from "@core/i18n/i18n-field-response";
import { IFormularioResponse } from "../formulario/formulario-response";

export interface IBloqueResponse {
  /** Id */
  id: number;
  /** Formulario */
  formulario: IFormularioResponse;
  /** orden */
  orden: number;
  /** bloque language */
  nombre: I18nFieldValueResponse[];
}
