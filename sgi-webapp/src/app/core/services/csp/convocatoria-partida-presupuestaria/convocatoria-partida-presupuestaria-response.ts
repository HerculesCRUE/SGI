import { TipoPartida } from "@core/enums/tipo-partida";

export interface IConvocatoriaPartidaPresupuestariaResponse {
  id: number;
  convocatoriaId: number;
  codigo: string;
  partidaRef: string;
  descripcion: string;
  tipoPartida: TipoPartida;
}
