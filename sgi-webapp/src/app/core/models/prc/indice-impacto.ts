import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { IProduccionCientifica } from './produccion-cientifica';

export interface IIndiceImpacto {
  id: number;
  ranking: TipoRanking;
  anio: number;
  otraFuenteImpacto: string;
  indice: number;
  posicionPublicacion: number;
  numeroRevistas: number;
  revista25: boolean;
  produccionCientifica: IProduccionCientifica;
  tipoFuenteImpacto: string;
}

export enum TipoRanking {
  /** Clase1 */
  CLASE1 = 'CLASE1',
  /** Clase2 */
  CLASE2 = 'CLASE2',
  /** Clase3 */
  CLASE3 = 'CLASE3',
  /** A* */
  A_POR = 'A_POR',
  /** A */
  A = 'A'
}

export const TIPO_RANKING_MAP: Map<TipoRanking, string> = new Map([
  [TipoRanking.CLASE1, marker('prc.indice-impacto.tipo-ranking.CLASE1')],
  [TipoRanking.CLASE2, marker('prc.indice-impacto.tipo-ranking.CLASE2')],
  [TipoRanking.CLASE3, marker('prc.indice-impacto.tipo-ranking.CLASE3')],
  [TipoRanking.A_POR, marker('prc.indice-impacto.tipo-ranking.A_POR')],
  [TipoRanking.A, marker('prc.indice-impacto.tipo-ranking.A')],
]);

export enum Cuartil {
  Q1 = 'Q1',
  Q2 = 'Q2',
  Q3 = 'Q3',
  Q4 = 'Q4'
}

export enum TipoFuenteImpacto {
  /** WOS (JCR) */
  WOS_JCR = '000',
  /** SCOPUS (SJR) */
  SCOPUS_SJR = '010',
  /** INRECS */
  INRECS = '020',
  /** BCI */
  BCI = 'BCI',
  /** ICEE */
  ICEE = 'ICEE',
  /** DIALNET */
  DIALNET = 'DIALNET',
  /** CitEC */
  CITEC = 'CITEC',
  /** SCIMAGO */
  SCIMAGO = 'SCIMAGO',
  /** ERIH */
  ERIH = 'ERIH',
  /** MIAR */
  MIAR = 'MIAR',
  /** FECYT */
  FECYT = 'FECYT',
  /** GII-GRIN-SCIE */
  GII_GRIN_SCIE = 'GII-GRIN-SCIE',
  /** CORE */
  CORE = 'CORE',
  /** Otros */
  OTHERS = 'OTHERS'
}

export const TIPO_FUENTE_IMPACTO_TRANSLATOR_MAP = new Map(Object.entries(TipoFuenteImpacto).map(([key, value]) => [value.toString(), key]));

export const TIPO_FUENTE_IMPACTO_MAP: Map<TipoFuenteImpacto, string> = new Map([
  [TipoFuenteImpacto.WOS_JCR, marker('prc.indice-impacto.tipo-fuente-impacto.WOS_JCR')],
  [TipoFuenteImpacto.SCOPUS_SJR, marker('prc.indice-impacto.tipo-fuente-impacto.SCOPUS_SJR')],
  [TipoFuenteImpacto.INRECS, marker('prc.indice-impacto.tipo-fuente-impacto.INRECS')],
  [TipoFuenteImpacto.BCI, marker('prc.indice-impacto.tipo-fuente-impacto.BCI')],
  [TipoFuenteImpacto.ICEE, marker('prc.indice-impacto.tipo-fuente-impacto.ICEE')],
  [TipoFuenteImpacto.DIALNET, marker('prc.indice-impacto.tipo-fuente-impacto.DIALNET')],
  [TipoFuenteImpacto.CITEC, marker('prc.indice-impacto.tipo-fuente-impacto.CITEC')],
  [TipoFuenteImpacto.SCIMAGO, marker('prc.indice-impacto.tipo-fuente-impacto.SCIMAGO')],
  [TipoFuenteImpacto.ERIH, marker('prc.indice-impacto.tipo-fuente-impacto.ERIH')],
  [TipoFuenteImpacto.MIAR, marker('prc.indice-impacto.tipo-fuente-impacto.MIAR')],
  [TipoFuenteImpacto.FECYT, marker('prc.indice-impacto.tipo-fuente-impacto.FECYT')],
  [TipoFuenteImpacto.GII_GRIN_SCIE, marker('prc.indice-impacto.tipo-fuente-impacto.GII_GRIN_SCIE')],
  [TipoFuenteImpacto.CORE, marker('prc.indice-impacto.tipo-fuente-impacto.CORE')],
  [TipoFuenteImpacto.OTHERS, marker('prc.indice-impacto.tipo-fuente-impacto.OTHERS')],
]);
