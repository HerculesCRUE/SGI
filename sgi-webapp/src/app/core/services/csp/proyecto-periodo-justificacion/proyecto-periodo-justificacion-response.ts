import { TipoJustificacion } from '@core/enums/tipo-justificacion';
import { I18nFieldValueResponse } from '@core/i18n/i18n-field-response';
import { IEstadoProyectoPeriodoJustificacion } from '@core/models/csp/estado-proyecto-periodo-justificacion';

export interface IProyectoPeriodoJustificacionResponse {
  id: number;
  proyectoId: number;
  numPeriodo: number;
  fechaInicio: string;
  fechaFin: string;
  fechaInicioPresentacion: string;
  fechaFinPresentacion: string;
  tipoJustificacion: TipoJustificacion;
  observaciones: I18nFieldValueResponse[];
  convocatoriaPeriodoJustificacionId: number;
  fechaPresentacionJustificacion: string;
  identificadorJustificacion: string;
  estado: IEstadoProyectoPeriodoJustificacion;
}
