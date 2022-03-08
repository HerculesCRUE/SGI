import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { DateTime } from 'luxon';
import { IGrupo } from './grupo';

export interface IGrupoTipo {
  id: number;
  tipo: Tipo;
  fechaInicio: DateTime;
  fechaFin: DateTime;
  grupo: IGrupo;
}

export enum Tipo {
  /** Emergente */
  EMERGENTE = 'EMERGENTE',
  /** Consolidado */
  CONSOLIDADO = 'CONSOLIDADO',
  /** Precompetitivo */
  PRECOMPETITIVO = 'PRECOMPETITIVO',
  /** Grupo de alto rendimiento */
  ALTO_RENDIMIENTO = 'ALTO_RENDIMIENTO'
}

export const TIPO_MAP: Map<Tipo, string> = new Map([
  [Tipo.EMERGENTE, marker(`csp.grupo-tipo.EMERGENTE`)],
  [Tipo.CONSOLIDADO, marker(`csp.grupo-tipo.CONSOLIDADO`)],
  [Tipo.PRECOMPETITIVO, marker(`csp.grupo-tipo.PRECOMPETITIVO`)],
  [Tipo.ALTO_RENDIMIENTO, marker(`csp.grupo-tipo.ALTO_RENDIMIENTO`)]
]);
