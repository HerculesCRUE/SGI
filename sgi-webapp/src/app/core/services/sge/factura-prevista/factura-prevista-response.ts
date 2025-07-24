import { I18nFieldValueResponse } from "@core/i18n/i18n-field-response";

export interface IFacturaPrevistaResponse {
  id: string;
  proyectoIdSGI: number;
  proyectoSgeId: string;
  numeroPrevision: number;
  fechaEmision: string;
  importeBase: number;
  porcentajeIVA: number;
  comentario: I18nFieldValueResponse[];
  tipoFacturacion: I18nFieldValueResponse[];
}
