import { TipoSeguimiento } from '@core/enums/tipo-seguimiento';
import { DateTime } from 'luxon';

export interface IProyectoPeriodoSeguimiento {
  id: number;
  proyectoId: number;
  numPeriodo: number;
  fechaInicio: DateTime;
  fechaFin: DateTime;
  fechaInicioPresentacion: DateTime;
  fechaFinPresentacion: DateTime;
  tipoSeguimiento: TipoSeguimiento;
  observaciones: string;
  convocatoriaPeriodoSeguimientoId: number;
}
