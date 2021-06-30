import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { DateTime } from 'luxon';

export interface IProyectoProrroga {
  id: number;
  proyectoId: number;
  numProrroga: number;
  fechaConcesion: DateTime;
  tipo: Tipo;
  fechaFin: DateTime;
  importe: number;
  observaciones: string;
}

export enum Tipo {
  TIEMPO = 'TIEMPO',
  IMPORTE = 'IMPORTE',
  TIEMPO_IMPORTE = 'TIEMPO_IMPORTE'
}

export const TIPO_MAP: Map<Tipo, string> = new Map([
  [Tipo.TIEMPO, marker(`csp.prorroga.tipo.TIEMPO`)],
  [Tipo.IMPORTE, marker(`csp.prorroga.tipo.IMPORTE`)],
  [Tipo.TIEMPO_IMPORTE, marker(`csp.prorroga.tipo.TIEMPO_IMPORTE`)]
]);
