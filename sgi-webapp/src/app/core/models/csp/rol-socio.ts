import { I18nFieldValue } from "@core/i18n/i18n-field";

export interface IRolSocio {
  id: number;
  abreviatura: I18nFieldValue[];
  nombre: I18nFieldValue[];
  descripcion: I18nFieldValue[];
  coordinador: boolean;
  activo: boolean;
}
