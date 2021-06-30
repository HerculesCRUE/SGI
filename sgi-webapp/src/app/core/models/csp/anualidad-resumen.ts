import { TipoPartida } from '@core/enums/tipo-partida';

export interface IAnualidadResumen {
  tipo: TipoPartida;
  codigoPartidaPresupuestaria: string;
  importePresupuesto: number;
  importeConcedido: number;
}
