import { DateTime } from 'luxon';

export interface IProyectoSocioPeriodoJustificacion {
  id: number;
  proyectoSocioId: number;
  numPeriodo: number;
  fechaInicio: DateTime;
  fechaFin: DateTime;
  fechaInicioPresentacion: DateTime;
  fechaFinPresentacion: DateTime;
  observaciones: string;
  documentacionRecibida: boolean;
  fechaRecepcion: DateTime;
  importeJustificado: number;
}
