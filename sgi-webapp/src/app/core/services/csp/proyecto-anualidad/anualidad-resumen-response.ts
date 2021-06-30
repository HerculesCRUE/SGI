import { TipoPartida } from '@core/enums/tipo-partida';

export interface IAnualidadResumenResponse {
  /** Tipo */
  tipo: TipoPartida;
  /** Código partida presupuestaria */
  codigoPartidaPresupuestaria: string;
  /** Importe presupuesto */
  importePresupuesto: number;
  /** Importe concedido */
  importeConcedido: number;
}
