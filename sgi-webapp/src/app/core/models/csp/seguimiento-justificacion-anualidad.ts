import { DateTime } from 'luxon';
import { IProyectoPeriodoJustificacionSeguimiento } from './proyecto-periodo-justificacion-seguimiento';

export interface ISeguimientoJustificacionAnualidad {
  proyectoPeriodoJustificacionId: number;
  proyectoId: number;
  proyectoPeriodoJustificacionSeguimiento: IProyectoPeriodoJustificacionSeguimiento;
  identificadorJustificacion: string;
  fechaPresentacionJustificacion: DateTime;
  anio: number;
}
