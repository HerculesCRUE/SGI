import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { I18nFieldValue } from '@core/i18n/i18n-field';
import { DateTime } from 'luxon';

export interface IEstadoGastoProyecto {
  id: number;
  estado: Estado;
  fechaEstado: DateTime;
  comentario: I18nFieldValue[];
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
