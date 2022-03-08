import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { DateTime } from 'luxon';

export interface IEstadoProduccionCientifica {
  id: number;
  estado: TipoEstadoProduccion;
  fecha: DateTime;
  comentario: string;
}

export enum TipoEstadoProduccion {
  /** Pendiente */
  PENDIENTE = 'PENDIENTE',
  /** Validado */
  VALIDADO = 'VALIDADO',
  /** Validado parcíalmente */
  VALIDADO_PARCIALMENTE = 'VALIDADO_PARCIALMENTE',
  /** Rechazado */
  RECHAZADO = 'RECHAZADO'
}

export const TIPO_ESTADO_PRODUCCION_MAP: Map<TipoEstadoProduccion, string> = new Map([
  [TipoEstadoProduccion.PENDIENTE, marker('prc.estado-produccion-cientifica.tipo-estado-produccion.PENDIENTE')],
  [TipoEstadoProduccion.VALIDADO, marker('prc.estado-produccion-cientifica.tipo-estado-produccion.VALIDADO')],
  [TipoEstadoProduccion.VALIDADO_PARCIALMENTE, marker('prc.estado-produccion-cientifica.tipo-estado-produccion.VALIDADO_PARCIALMENTE')],
  [TipoEstadoProduccion.RECHAZADO, marker('prc.estado-produccion-cientifica.tipo-estado-produccion.RECHAZADO')]
]);
