import { marker } from '@biesbjerg/ngx-translate-extract-marker';

export interface ITramoReparto {
  id: number;
  desde: number;
  hasta: number;
  porcentajeUniversidad: number;
  porcentajeInventores: number;
  tipo: Tipo;
  activo: boolean;
}

export enum Tipo {
  /** Inicial */
  INICIAL = 'INICIAL',
  /** Intermedio */
  INTERMEDIO = 'INTERMEDIO',
  /** Final */
  FINAL = 'FINAL'
}

export const TIPO_TRAMO_REPARTO_MAP: Map<Tipo, string> = new Map([
  [Tipo.INICIAL, marker('pii.tramo-reparto.tipo.INICIAL')],
  [Tipo.INTERMEDIO, marker('pii.tramo-reparto.tipo.INTERMEDIO')],
  [Tipo.FINAL, marker('pii.tramo-reparto.tipo.FINAL')]
]);