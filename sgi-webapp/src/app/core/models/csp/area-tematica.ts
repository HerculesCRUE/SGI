import { I18nFieldValue } from "@core/i18n/i18n-field";

export interface IAreaTematica {
  id: number;
  nombre: I18nFieldValue[];
  descripcion: I18nFieldValue[];
  padre: IAreaTematica;
  activo: boolean;
}

