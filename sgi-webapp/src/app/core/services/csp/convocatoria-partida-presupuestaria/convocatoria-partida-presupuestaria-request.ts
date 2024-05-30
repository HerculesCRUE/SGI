import { TipoPartida } from "@core/enums/tipo-partida";

export interface IConvocatoriaPartidaPresupuestariaRequest {
  convocatoriaId: number;
  codigo: string;
  partidaRef: string;
  descripcion: string;
  tipoPartida: TipoPartida;
}
