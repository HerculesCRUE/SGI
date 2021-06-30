import { marker } from '@biesbjerg/ngx-translate-extract-marker';

export enum TipoPartida {
  GASTO = 'GASTO',
  INGRESO = 'INGRESO'
}

export const TIPO_PARTIDA_MAP: Map<TipoPartida, string> = new Map([
  [TipoPartida.GASTO, marker('csp.tipo-partida.GASTO')],
  [TipoPartida.INGRESO, marker('csp.tipo-partida.INGRESO')]
]);
