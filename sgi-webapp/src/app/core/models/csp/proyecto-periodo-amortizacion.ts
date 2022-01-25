import { DateTime } from 'luxon';
import { IProyectoSge } from '../sge/proyecto-sge';
import { IProyectoAnualidad } from './proyecto-anualidad';
import { IProyectoEntidadFinanciadora } from './proyecto-entidad-financiadora';

export interface IProyectoPeriodoAmortizacion {
  id: number;
  importe: number;
  fechaLimiteAmortizacion: DateTime;
  proyectoSGE: IProyectoSge;
  proyectoEntidadFinanciadora: IProyectoEntidadFinanciadora;
  proyectoAnualidad: IProyectoAnualidad
}
