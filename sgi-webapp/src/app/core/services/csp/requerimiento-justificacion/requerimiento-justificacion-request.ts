import { I18nFieldValueRequest } from "@core/i18n/i18n-field-request";

export interface IRequerimientoJustificacionRequest {
  proyectoProyectoSgeId: number;
  tipoRequerimientoId: number;
  proyectoPeriodoJustificacionId: number;
  requerimientoPrevioId: number;
  fechaNotificacion: string;
  fechaFinAlegacion: string;
  observaciones: I18nFieldValueRequest[];
  importeAceptadoCd: number;
  importeAceptadoCi: number;
  importeRechazadoCd: number;
  importeRechazadoCi: number;
  importeReintegrar: number;
  importeReintegrarCd: number;
  importeReintegrarCi: number;
  interesesReintegrar: number;
  importeAceptado: number;
  importeRechazado: number;
  subvencionJustificada: number;
  defectoSubvencion: number;
  anticipoJustificado: number;
  defectoAnticipo: number;
  recursoEstimado: boolean;
}
