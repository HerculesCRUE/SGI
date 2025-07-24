import { I18nFieldValue } from "@core/i18n/i18n-field";

export interface IPrograma {
  /** Id */
  id: number;

  /** Nombre  */
  nombre: I18nFieldValue[];

  /** descripcion  */
  descripcion: I18nFieldValue[];

  /** padre  */
  padre: IPrograma;

  /** activo  */
  activo: boolean;
}
