import { TipoRanking } from '@core/models/prc/indice-impacto';

export interface IIndiceImpactoResponse {
  id: number;
  ranking: TipoRanking;
  anio: number;
  otraFuenteImpacto: string;
  indice: number;
  posicionPublicacion: number;
  numeroRevistas: number;
  revista25: boolean;
  produccionCientificaId: number;
  tipoFuenteImpacto: string;
}
