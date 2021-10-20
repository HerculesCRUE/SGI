import { IProyectoSge } from './proyecto-sge';

export interface IProyectoAnualidadPartida {
  proyecto: IProyectoSge;
  anualidad: number;
  tipoDatoEconomico: string;
  partidaPresupuestaria: string;
  importe: number;
}
