import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { DateTime } from 'luxon';

export interface IEstadoGastoProyecto {
  id: number;
  estado: Estado;
  fechaEstado: DateTime;
  comentario: string;
  gastoProyectoId: number;
}

export enum Estado {
  VALIDADO = 'VALIDADO',
  BLOQUEADO = 'BLOQUEADO',
  RECHAZADO = 'RECHAZADO'
}

export const ESTADO_MAP: Map<Estado, string> = new Map([
  [Estado.VALIDADO, marker(`csp.estado-gasto-proyecto.VALIDADO`)],
  [Estado.BLOQUEADO, marker(`csp.estado-gasto-proyecto.BLOQUEADO`)],
  [Estado.RECHAZADO, marker(`csp.estado-gasto-proyecto.RECHAZADO`)]
]);
