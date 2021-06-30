
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { DateTime } from 'luxon';

export interface IEstadoSolicitud {
  /** Id */
  id: number;
  /** ID de la solicitud */
  solicitudId: number;
  /** Estado */
  estado: Estado;
  /** Fecha estado */
  fechaEstado: DateTime;
  /** Comentario */
  comentario: string;
}

export enum Estado {
  BORRADOR = 'BORRADOR',
  SOLICITADA = 'SOLICITADA',
  SUBSANACION = 'SUBSANACION',
  PRESENTADA_SUBSANACION = 'PRESENTADA_SUBSANACION',
  EXCLUIDA_PROVISIONAL = 'EXCLUIDA_PROVISIONAL',
  ADMITIDA_PROVISIONAL = 'ADMITIDA_PROVISIONAL',
  ALEGACION_FASE_ADMISION = 'ALEGACION_FASE_ADMISION',
  ADMITIDA_DEFINITIVA = 'ADMITIDA_DEFINITIVA',
  EXCLUIDA_DEFINITIVA = 'EXCLUIDA_DEFINITIVA',
  RECURSO_FASE_ADMISION = 'RECURSO_FASE_ADMISION',
  CONCEDIDA_PROVISIONAL = 'CONCEDIDA_PROVISIONAL',
  DENEGADA_PROVISIONAL = 'DENEGADA_PROVISIONAL',
  ALEGACION_FASE_PROVISIONAL = 'ALEGACION_FASE_PROVISIONAL',
  CONCEDIDA_PROVISIONAL_ALEGADA = 'CONCEDIDA_PROVISIONAL_ALEGADA',
  CONCEDIDA_PROVISIONAL_NO_ALEGADA = 'CONCEDIDA_PROVISIONAL_NO_ALEGADA',
  DENEGADA_PROVISIONAL_ALEGADA = 'DENEGADA_PROVISIONAL_ALEGADA',
  DENEGADA_PROVISIONAL_NO_ALEGADA = 'DENEGADA_PROVISIONAL_NO_ALEGADA',
  DESISTIDA = 'DESISTIDA',
  RESERVA_PROVISIONAL = 'RESERVA_PROVISIONAL',
  NEGOCIACION = 'NEGOCIACION',
  CONCEDIDA = 'CONCEDIDA',
  DENEGADA = 'DENEGADA',
  RECURSO_FASE_CONCESION = 'RECURSO_FASE_CONCESION',
  RESERVA = 'RESERVA',
  FIRMADA = 'FIRMADA',
  RENUNCIADA = 'RENUNCIADA'
}

export const ESTADO_MAP: Map<Estado, string> = new Map([
  [Estado.BORRADOR, marker(`csp.estado-solicitud.BORRADOR`)],
  [Estado.SOLICITADA, marker(`csp.estado-solicitud.SOLICITADA`)],
  [Estado.SUBSANACION, marker(`csp.estado-solicitud.SUBSANACION`)],
  [Estado.PRESENTADA_SUBSANACION, marker(`csp.estado-solicitud.PRESENTADA_SUBSANACION`)],
  [Estado.EXCLUIDA_PROVISIONAL, marker(`csp.estado-solicitud.EXCLUIDA_PROVISIONAL`)],
  [Estado.ADMITIDA_PROVISIONAL, marker(`csp.estado-solicitud.ADMITIDA_PROVISIONAL`)],
  [Estado.ALEGACION_FASE_ADMISION, marker(`csp.estado-solicitud.ALEGACION_FASE_ADMISION`)],
  [Estado.ADMITIDA_DEFINITIVA, marker(`csp.estado-solicitud.ADMITIDA_DEFINITIVA`)],
  [Estado.EXCLUIDA_DEFINITIVA, marker(`csp.estado-solicitud.EXCLUIDA_DEFINITIVA`)],
  [Estado.RECURSO_FASE_ADMISION, marker(`csp.estado-solicitud.RECURSO_FASE_ADMISION`)],
  [Estado.CONCEDIDA_PROVISIONAL, marker(`csp.estado-solicitud.CONCEDIDA_PROVISIONAL`)],
  [Estado.DENEGADA_PROVISIONAL, marker(`csp.estado-solicitud.DENEGADA_PROVISIONAL`)],
  [Estado.ALEGACION_FASE_PROVISIONAL, marker(`csp.estado-solicitud.ALEGACION_FASE_PROVISIONAL`)],
  [Estado.CONCEDIDA_PROVISIONAL_ALEGADA, marker(`csp.estado-solicitud.CONCEDIDA_PROVISIONAL_ALEGADA`)],
  [Estado.CONCEDIDA_PROVISIONAL_NO_ALEGADA, marker(`csp.estado-solicitud.CONCEDIDA_PROVISIONAL_NO_ALEGADA`)],
  [Estado.DENEGADA_PROVISIONAL_NO_ALEGADA, marker(`csp.estado-solicitud.DENEGADA_PROVISIONAL_NO_ALEGADA`)],
  [Estado.DESISTIDA, marker(`csp.estado-solicitud.DESISTIDA`)],
  [Estado.RESERVA_PROVISIONAL, marker(`csp.estado-solicitud.RESERVA_PROVISIONAL`)],
  [Estado.NEGOCIACION, marker(`csp.estado-solicitud.NEGOCIACION`)],
  [Estado.CONCEDIDA, marker(`csp.estado-solicitud.CONCEDIDA`)],
  [Estado.DENEGADA, marker(`csp.estado-solicitud.DENEGADA`)],
  [Estado.RECURSO_FASE_CONCESION, marker(`csp.estado-solicitud.RECURSO_FASE_CONCESION`)],
  [Estado.RESERVA, marker(`csp.estado-solicitud.RESERVA`)],
  [Estado.FIRMADA, marker(`csp.estado-solicitud.FIRMADA`)],
  [Estado.RENUNCIADA, marker(`csp.estado-solicitud.RENUNCIADA`)]
]);
