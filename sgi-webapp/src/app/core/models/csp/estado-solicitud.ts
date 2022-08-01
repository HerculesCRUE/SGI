
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
  ADMITIDA_DEFINITIVA = 'ADMITIDA_DEFINITIVA',
  ADMITIDA_PROVISIONAL = 'ADMITIDA_PROVISIONAL',
  ALEGACION_FASE_ADMISION = 'ALEGACION_FASE_ADMISION',
  ALEGACION_FASE_PROVISIONAL = 'ALEGACION_FASE_PROVISIONAL',
  BORRADOR = 'BORRADOR',
  CONCEDIDA = 'CONCEDIDA',
  CONCEDIDA_PROVISIONAL = 'CONCEDIDA_PROVISIONAL',
  CONCEDIDA_PROVISIONAL_ALEGADA = 'CONCEDIDA_PROVISIONAL_ALEGADA',
  CONCEDIDA_PROVISIONAL_NO_ALEGADA = 'CONCEDIDA_PROVISIONAL_NO_ALEGADA',
  DENEGADA = 'DENEGADA',
  DENEGADA_PROVISIONAL = 'DENEGADA_PROVISIONAL',
  DENEGADA_PROVISIONAL_ALEGADA = 'DENEGADA_PROVISIONAL_ALEGADA',
  DENEGADA_PROVISIONAL_NO_ALEGADA = 'DENEGADA_PROVISIONAL_NO_ALEGADA',
  DESISTIDA = 'DESISTIDA',
  EXCLUIDA_DEFINITIVA = 'EXCLUIDA_DEFINITIVA',
  EXCLUIDA_PROVISIONAL = 'EXCLUIDA_PROVISIONAL',
  FIRMADA = 'FIRMADA',
  NEGOCIACION = 'NEGOCIACION',
  PRESENTADA_SUBSANACION = 'PRESENTADA_SUBSANACION',
  RECURSO_FASE_ADMISION = 'RECURSO_FASE_ADMISION',
  RECURSO_FASE_CONCESION = 'RECURSO_FASE_CONCESION',
  RENUNCIADA = 'RENUNCIADA',
  RESERVA = 'RESERVA',
  RESERVA_PROVISIONAL = 'RESERVA_PROVISIONAL',
  SOLICITADA = 'SOLICITADA',
  SUBSANACION = 'SUBSANACION',
  RECHAZADA = 'RECHAZADA',
  VALIDADA = 'VALIDADA'
}

export const ESTADO_MAP: Map<Estado, string> = new Map([
  [Estado.ADMITIDA_DEFINITIVA, marker(`csp.estado-solicitud.ADMITIDA_DEFINITIVA`)],
  [Estado.ADMITIDA_PROVISIONAL, marker(`csp.estado-solicitud.ADMITIDA_PROVISIONAL`)],
  [Estado.ALEGACION_FASE_ADMISION, marker(`csp.estado-solicitud.ALEGACION_FASE_ADMISION`)],
  [Estado.ALEGACION_FASE_PROVISIONAL, marker(`csp.estado-solicitud.ALEGACION_FASE_PROVISIONAL`)],
  [Estado.BORRADOR, marker(`csp.estado-solicitud.BORRADOR`)],
  [Estado.CONCEDIDA, marker(`csp.estado-solicitud.CONCEDIDA`)],
  [Estado.CONCEDIDA_PROVISIONAL, marker(`csp.estado-solicitud.CONCEDIDA_PROVISIONAL`)],
  [Estado.CONCEDIDA_PROVISIONAL_ALEGADA, marker(`csp.estado-solicitud.CONCEDIDA_PROVISIONAL_ALEGADA`)],
  [Estado.CONCEDIDA_PROVISIONAL_NO_ALEGADA, marker(`csp.estado-solicitud.CONCEDIDA_PROVISIONAL_NO_ALEGADA`)],
  [Estado.DENEGADA, marker(`csp.estado-solicitud.DENEGADA`)],
  [Estado.DENEGADA_PROVISIONAL, marker(`csp.estado-solicitud.DENEGADA_PROVISIONAL`)],
  [Estado.DENEGADA_PROVISIONAL_ALEGADA, marker(`csp.estado-solicitud.DENEGADA_PROVISIONAL_ALEGADA`)],
  [Estado.DENEGADA_PROVISIONAL_NO_ALEGADA, marker(`csp.estado-solicitud.DENEGADA_PROVISIONAL_NO_ALEGADA`)],
  [Estado.DESISTIDA, marker(`csp.estado-solicitud.DESISTIDA`)],
  [Estado.EXCLUIDA_DEFINITIVA, marker(`csp.estado-solicitud.EXCLUIDA_DEFINITIVA`)],
  [Estado.EXCLUIDA_PROVISIONAL, marker(`csp.estado-solicitud.EXCLUIDA_PROVISIONAL`)],
  [Estado.FIRMADA, marker(`csp.estado-solicitud.FIRMADA`)],
  [Estado.NEGOCIACION, marker(`csp.estado-solicitud.NEGOCIACION`)],
  [Estado.PRESENTADA_SUBSANACION, marker(`csp.estado-solicitud.PRESENTADA_SUBSANACION`)],
  [Estado.RECURSO_FASE_ADMISION, marker(`csp.estado-solicitud.RECURSO_FASE_ADMISION`)],
  [Estado.RECURSO_FASE_CONCESION, marker(`csp.estado-solicitud.RECURSO_FASE_CONCESION`)],
  [Estado.RENUNCIADA, marker(`csp.estado-solicitud.RENUNCIADA`)],
  [Estado.RESERVA, marker(`csp.estado-solicitud.RESERVA`)],
  [Estado.RESERVA_PROVISIONAL, marker(`csp.estado-solicitud.RESERVA_PROVISIONAL`)],
  [Estado.SOLICITADA, marker(`csp.estado-solicitud.SOLICITADA`)],
  [Estado.SUBSANACION, marker(`csp.estado-solicitud.SUBSANACION`)],
  [Estado.RECHAZADA, marker(`csp.estado-solicitud.RECHAZADA`)],
  [Estado.VALIDADA, marker(`csp.estado-solicitud.VALIDADA`)],
]);
