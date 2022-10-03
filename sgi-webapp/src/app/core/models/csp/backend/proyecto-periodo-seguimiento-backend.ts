import { TipoSeguimiento } from '@core/enums/tipo-seguimiento';
import { DateTime } from 'luxon';

export interface IProyectoPeriodoSeguimientoBackend {
  id: number;
  proyectoId: number;
  numPeriodo: number;
  fechaInicio: string;
  fechaFin: string;
  fechaInicioPresentacion: string;
  fechaFinPresentacion: string;
  tipoSeguimiento: TipoSeguimiento;
  observaciones: string;
  convocatoriaPeriodoSeguimientoId: number;
  fechaPresentacionDocumentacion: DateTime;
}
