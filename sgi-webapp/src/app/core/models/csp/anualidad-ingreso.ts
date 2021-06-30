import { IProyectoAnualidad } from './proyecto-anualidad';
import { IProyectoPartida } from './proyecto-partida';

export interface IAnualidadIngreso {
  id: number;
  proyectoAnualidad: IProyectoAnualidad;
  codigoEconomicoRef: string;
  proyectoPartida: IProyectoPartida;
  importeConcedido: number;
  proyectoSgeRef: string;
}
