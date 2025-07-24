import { I18nFieldValueResponse } from "@core/i18n/i18n-field-response";

export interface ISolicitudProyectoSocioPeriodoJustificacionResponse {
  id: number;
  solicitudProyectoSocioId: number;
  numPeriodo: number;
  mesInicial: number;
  mesFinal: number;
  fechaInicio: string;
  fechaFin: string;
  observaciones: I18nFieldValueResponse[];
}
