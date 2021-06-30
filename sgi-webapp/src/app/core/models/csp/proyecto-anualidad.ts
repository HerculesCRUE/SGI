import { DateTime } from 'luxon';
import { IProyecto } from './proyecto';

export interface IProyectoAnualidad {
  id: number;
  proyecto: IProyecto;
  anio: number;
  fechaInicio: DateTime;
  fechaFin: DateTime;
  presupuestar: boolean;
  enviadoSge: boolean;
}
