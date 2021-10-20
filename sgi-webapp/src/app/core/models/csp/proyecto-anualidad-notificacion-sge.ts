import { DateTime } from 'luxon';

export interface IProyectoAnualidadNotificacionSge {
  id: number;
  anio: number;
  proyectoFechaInicio: DateTime;
  proyectoFechaFin: DateTime;
  totalGastos: number;
  totalIngresos: number;
  proyectoId: number;
  proyectoTitulo: string;
  proyectoAcronimo: string;
  proyectoEstado: string;
  proyectoSgeRef: string;
  enviadoSge: boolean;
}
