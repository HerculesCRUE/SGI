import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { I18nFieldValue } from '@core/i18n/i18n-field';
import { DateTime } from 'luxon';
import { IAutorizacion } from './autorizacion';

export interface IEstadoAutorizacion {
  id: number;
  autorizacion: IAutorizacion;
  comentario: I18nFieldValue[];
  fecha: DateTime;
  estado: Estado;
}

export enum Estado {
  /** Borrador */
  BORRADOR = 'BORRADOR',
  /** Presentada */
  PRESENTADA = 'PRESENTADA',
  /** Autorizada */
  AUTORIZADA = 'AUTORIZADA',
  /** Revisión */
  REVISION = 'REVISION'
}

export const ESTADO_MAP: Map<Estado, string> = new Map([
  [Estado.BORRADOR, marker(`csp.estado-autorizacion.BORRADOR`)],
  [Estado.PRESENTADA, marker(`csp.estado-autorizacion.PRESENTADA`)],
  [Estado.AUTORIZADA, marker(`csp.estado-autorizacion.AUTORIZADA`)],
  [Estado.REVISION, marker(`csp.estado-autorizacion.REVISION`)]
]);
