import { marker } from '@biesbjerg/ngx-translate-extract-marker';

export enum TipoJustificacion {
  PERIODICO = 'PERIODICO',
  INTERMEDIO = 'INTERMEDIO',
  FINAL = 'FINAL'
}

export const TIPO_JUSTIFICACION_MAP: Map<TipoJustificacion, string> = new Map([
  [TipoJustificacion.PERIODICO, marker('csp.tipo-seguimiento.PERIODICO')],
  [TipoJustificacion.INTERMEDIO, marker('csp.tipo-seguimiento.INTERMEDIO')],
  [TipoJustificacion.FINAL, marker('csp.tipo-seguimiento.FINAL')]
]);
