import { I18nFieldValue } from "@core/i18n/i18n-field";

export interface ITipoFacturacion {
  id: number;
  nombre: I18nFieldValue[];
  incluirEnComunicado: boolean;
  activo: boolean;
}
