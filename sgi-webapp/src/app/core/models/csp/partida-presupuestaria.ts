import { TipoPartida } from '@core/enums/tipo-partida';
import { IPartidaPresupuestariaSge } from '../sge/partida-presupuestaria-sge';

export interface IPartidaPresupuestaria {
  id: number;
  codigo: string;
  partidaSge: IPartidaPresupuestariaSge;
  descripcion: string;
  tipoPartida: TipoPartida;
}
