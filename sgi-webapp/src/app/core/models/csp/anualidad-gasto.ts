import { ICodigoEconomicoGasto } from '../sge/codigo-economico-gasto';
import { IConceptoGasto } from './concepto-gasto';
import { IProyectoAnualidad } from './proyecto-anualidad';
import { IProyectoPartida } from './proyecto-partida';

export interface IAnualidadGasto {
  id: number;
  proyectoAnualidad: IProyectoAnualidad;
  conceptoGasto: IConceptoGasto;
  codigoEconomico: ICodigoEconomicoGasto;
  proyectoPartida: IProyectoPartida;
  importePresupuesto: number;
  importeConcedido: number;
  proyectoSgeRef: string;
}
