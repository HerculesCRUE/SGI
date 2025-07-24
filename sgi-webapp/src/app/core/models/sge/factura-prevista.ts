import { I18nFieldValue } from "@core/i18n/i18n-field";
import { DateTime } from "luxon";

export interface IFacturaPrevista {
  id: string;
  proyectoIdSGI: number;
  proyectoSgeId: string;
  numeroPrevision: number;
  fechaEmision: DateTime;
  importeBase: number;
  porcentajeIVA: number;
  comentario: I18nFieldValue[];
  tipoFacturacion: I18nFieldValue[];
}
