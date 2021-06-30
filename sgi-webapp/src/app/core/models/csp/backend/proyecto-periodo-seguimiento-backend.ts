import { TipoSeguimiento } from '@core/enums/tipo-seguimiento';

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
}
