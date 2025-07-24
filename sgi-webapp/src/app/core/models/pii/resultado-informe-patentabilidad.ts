import { I18nFieldValue } from "@core/i18n/i18n-field";

export interface IResultadoInformePatentibilidad {
  /** id */
  id: number;

  /** Nombre */
  nombre: I18nFieldValue[];

  /** Descripción */
  descripcion: I18nFieldValue[];

  /** Activo */
  activo: boolean;
}
