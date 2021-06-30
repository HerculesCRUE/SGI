import { IPartidaPresupuestaria } from './partida-presupuestaria';

export interface IProyectoPartida extends IPartidaPresupuestaria {
  proyectoId: number;
  convocatoriaPartidaId: number;
}
