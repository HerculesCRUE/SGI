import { TipoPartida } from '@core/enums/tipo-partida';

export interface IPartidaPresupuestaria {
  id: number;
  codigo: string;
  descripcion: string;
  tipoPartida: TipoPartida;
}
