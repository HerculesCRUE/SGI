import { DateTime } from 'luxon';
import { IConceptoGasto } from './concepto-gasto';
import { IProyectoAnualidad } from './proyecto-anualidad';
import { IProyectoPartida } from './proyecto-partida';

export interface IAnualidadGasto {
  id: number;
  proyectoAnualidad: IProyectoAnualidad;
  conceptoGasto: IConceptoGasto;
  codigoEconomicoRef: string;
  proyectoPartida: IProyectoPartida;
  importePresupuesto: number;
  importeConcedido: number;
  proyectoSgeRef: string;
}
