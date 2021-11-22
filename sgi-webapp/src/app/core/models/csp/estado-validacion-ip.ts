import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { DateTime } from 'luxon';

export interface IEstadoValidacionIP {
  id: number;
  comentario: string;
  estado: TipoEstadoValidacion;
  fecha: DateTime;
  proyectoFacturacionId: number;
}

export enum TipoEstadoValidacion {
  PENDIENTE = 'PENDIENTE',
  NOTIFICADA = 'NOTIFICADA',
  VALIDADA = 'VALIDADA',
  RECHAZADA = 'RECHAZADA'
}

export const TIPO_ESTADO_VALIDACION_MAP: Map<TipoEstadoValidacion, string> = new Map([
  [TipoEstadoValidacion.NOTIFICADA, marker('csp.tipo-estado-validacion.NOTIFICADA')],
  [TipoEstadoValidacion.PENDIENTE, marker('csp.tipo-estado-validacion.PENDIENTE')],
  [TipoEstadoValidacion.VALIDADA, marker('csp.tipo-estado-validacion.VALIDADA')],
  [TipoEstadoValidacion.RECHAZADA, marker('csp.tipo-estado-validacion.RECHAZADA')]
]);
