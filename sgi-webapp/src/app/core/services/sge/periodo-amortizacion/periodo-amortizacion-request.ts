import { I18nFieldValueRequest } from "@core/i18n/i18n-field-request";

export interface IPeriodoAmortizacionRequest {
  id: string;
  proyectoId: string;
  anualidad: string;
  empresaRef: string;
  tipoFinanciacion: {
    id: string,
    nombre: I18nFieldValueRequest[]
  },
  fuenteFinanciacion: {
    id: string,
    nombre: I18nFieldValueRequest[]
  },
  fecha: string;
  importe: number;
}