import { I18nFieldValue } from "@core/i18n/i18n-field";
import { I18nFieldValueResponse } from "@core/i18n/i18n-field-response";

export interface IProyectoSocioPeriodoJustificacionResponse {
  id: number;
  proyectoSocioId: number;
  numPeriodo: number;
  fechaInicio: string;
  fechaFin: string;
  fechaInicioPresentacion: string;
  fechaFinPresentacion: string;
  observaciones: I18nFieldValueResponse[];
  documentacionRecibida: boolean;
  fechaRecepcion: string;
  importeJustificado: number;
}
