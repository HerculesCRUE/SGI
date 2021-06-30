import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { DateTime } from 'luxon';

export interface IEstadoProyecto {
  /** Id */
  id: number;
  /** ID del proyecto */
  proyectoId: number;
  /** Estado */
  estado: Estado;
  /** Fecha estado */
  fechaEstado: DateTime;
  /** Comentario */
  comentario: string;
}

export enum Estado {
  BORRADOR = 'BORRADOR',
  CONCEDIDO = 'CONCEDIDO',
  RENUNCIADO = 'RENUNCIADO',
  RESCINDIDO = 'RESCINDIDO',
}

export const ESTADO_MAP: Map<Estado, string> = new Map([
  [Estado.BORRADOR, marker(`csp.estado-proyecto.BORRADOR`)],
  [Estado.CONCEDIDO, marker(`csp.estado-proyecto.CONCEDIDO`)],
  [Estado.RENUNCIADO, marker(`csp.estado-proyecto.RENUNCIADO`)],
  [Estado.RESCINDIDO, marker(`csp.estado-proyecto.RESCINDIDO`)]
]);
