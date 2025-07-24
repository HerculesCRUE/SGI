import { I18nFieldValue } from "@core/i18n/i18n-field";
export interface IConceptoGasto {

  /** Id */
  id: number;

  /** Nombre */
  nombre: I18nFieldValue[];

  /** Descripcion */
  descripcion: I18nFieldValue[];

  /** Contes Indirectos */
  costesIndirectos: boolean;

  /** Activo */
  activo: boolean;

}
