import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { DateTime } from 'luxon';

export interface IEstadoProyectoPeriodoJustificacion {
  id: number;
  estado: Estado;
  fechaEstado: DateTime;
  comentario: string;
  proyectoPeriodoJustificacionId: number;
}

export enum Estado {
  PENDIENTE = 'PENDIENTE',
  ELABORACION = 'ELABORACION',
  ENTREGA = 'ENTREGA',
  SUBSANACION = 'SUBSANACION',
  CERRADA = 'CERRADA'
}

export const ESTADO_MAP: Map<Estado, string> = new Map([
  [Estado.PENDIENTE, marker(`csp.estado-proyecto-periodo-justificacion.PENDIENTE`)],
  [Estado.ELABORACION, marker(`csp.estado-proyecto-periodo-justificacion.ELABORACION`)],
  [Estado.ENTREGA, marker(`csp.estado-proyecto-periodo-justificacion.ENTREGA`)],
  [Estado.SUBSANACION, marker(`csp.estado-proyecto-periodo-justificacion.SUBSANACION`)],
  [Estado.CERRADA, marker(`csp.estado-proyecto-periodo-justificacion.CERRADA`)]
]);
