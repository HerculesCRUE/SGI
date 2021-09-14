import { ICodigoEconomicoIngreso } from '../sge/codigo-economico-ingreso';
import { IProyectoAnualidad } from './proyecto-anualidad';
import { IProyectoPartida } from './proyecto-partida';

export interface IAnualidadIngreso {
  id: number;
  proyectoAnualidad: IProyectoAnualidad;
  codigoEconomico: ICodigoEconomicoIngreso;
  proyectoPartida: IProyectoPartida;
  importeConcedido: number;
  proyectoSgeRef: string;
}
