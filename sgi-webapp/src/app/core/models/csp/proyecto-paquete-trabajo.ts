import { DateTime } from 'luxon';

export interface IProyectoPaqueteTrabajo {
  /** Id */
  id: number;
  /** Id de Proyecto */
  proyectoId: number;
  /** nombre */
  nombre: string;
  /** fechaInicio */
  fechaInicio: DateTime;
  /** fechaFin */
  fechaFin: DateTime;
  /** personaMes */
  personaMes: number;
  /** descripcion */
  descripcion: string;
}
