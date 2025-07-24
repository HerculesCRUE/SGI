import { I18nFieldValueResponse } from "@core/i18n/i18n-field-response";

export interface IResultadoInformePatentibilidadResponse {
  /** id */
  id: number;

  /** Nombre */
  nombre: I18nFieldValueResponse[];

  /** Descripción */
  descripcion: I18nFieldValueResponse[];

  /** Activo */
  activo: boolean;
}
