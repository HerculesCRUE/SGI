import { I18nFieldValueRequest } from "@core/i18n/i18n-field-request";

export interface IAlegacionRequerimientoRequest {
  requerimientoJustificacionId: number;
  fechaAlegacion: string;
  importeAlegado: number;
  importeAlegadoCd: number;
  importeAlegadoCi: number;
  importeReintegrado: number;
  importeReintegradoCd: number;
  importeReintegradoCi: number;
  interesesReintegrados: number;
  fechaReintegro: string;
  justificanteReintegro: string;
  observaciones: I18nFieldValueRequest[];
}
