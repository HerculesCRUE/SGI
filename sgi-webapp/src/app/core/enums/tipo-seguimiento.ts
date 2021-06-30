import { marker } from '@biesbjerg/ngx-translate-extract-marker';

export enum TipoSeguimiento {
  PERIODICO = 'PERIODICO',
  INTERMEDIO = 'INTERMEDIO',
  FINAL = 'FINAL'
}

export const TIPO_SEGUIMIENTO_MAP: Map<TipoSeguimiento, string> = new Map([
  [TipoSeguimiento.PERIODICO, marker('csp.tipo-seguimiento.PERIODICO')],
  [TipoSeguimiento.INTERMEDIO, marker('csp.tipo-seguimiento.INTERMEDIO')],
  [TipoSeguimiento.FINAL, marker('csp.tipo-seguimiento.FINAL')]
]);
