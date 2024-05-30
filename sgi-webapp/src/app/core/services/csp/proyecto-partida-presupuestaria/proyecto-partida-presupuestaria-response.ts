import { TipoPartida } from "@core/enums/tipo-partida";

export interface IProyectoPartidaPresupuestariaResponse {
  id: number;
  proyectoId: number;
  convocatoriaPartidaId: number;
  codigo: string;
  partidaRef: string;
  descripcion: string;
  tipoPartida: TipoPartida;
}
