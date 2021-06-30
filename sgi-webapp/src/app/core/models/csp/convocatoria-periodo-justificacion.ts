import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { DateTime } from 'luxon';

export interface IConvocatoriaPeriodoJustificacion {
  /** Id */
  id: number;
  /** Id de Convocatoria */
  convocatoriaId: number;
  /** Num Periodo */
  numPeriodo: number;
  /** Mes inicial */
  mesInicial: number;
  /** Mes final */
  mesFinal: number;
  /** Fecha inicio */
  fechaInicioPresentacion: DateTime;
  /** Fecha fin */
  fechaFinPresentacion: DateTime;
  /** Observaciones */
  observaciones: string;
  /** Tipo */
  tipo: Tipo;
}

export enum Tipo {
  PERIODICO = 'PERIODICO',
  INTERMEDIO = 'INTERMEDIO',
  FINAL = 'FINAL'
}

export const TIPO_MAP: Map<Tipo, string> = new Map([
  [Tipo.PERIODICO, marker(`csp.convocatoria-periodo-justificacion.tipo.PERIODICO`)],
  [Tipo.INTERMEDIO, marker(`csp.convocatoria-periodo-justificacion.tipo.INTERMEDIO`)],
  [Tipo.FINAL, marker(`csp.convocatoria-periodo-justificacion.tipo.FINAL`)],
]);
