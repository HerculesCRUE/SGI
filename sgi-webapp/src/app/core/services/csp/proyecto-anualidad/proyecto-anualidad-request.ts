import { DateTime } from 'luxon';

export interface IProyectoAnualidadRequest {
  proyectoId: number;
  anio: number;
  fechaInicio: string;
  fechaFin: string;
  presupuestar: boolean;
  enviadoSge: boolean;
}
