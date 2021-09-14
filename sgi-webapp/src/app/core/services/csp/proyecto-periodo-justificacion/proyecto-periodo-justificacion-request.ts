import { TipoJustificacion } from '@core/enums/tipo-justificacion';

export interface IProyectoPeriodoJustificacionRequest {
  id: number;
  proyectoId: number;
  numPeriodo: number;
  fechaInicio: string;
  fechaFin: string;
  fechaInicioPresentacion: string;
  fechaFinPresentacion: string;
  tipoJustificacion: TipoJustificacion;
  observaciones: string;
  convocatoriaPeriodoJustificacionId: number;
}
