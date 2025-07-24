
import { I18nFieldValue } from '@core/i18n/i18n-field';
import { DateTime } from 'luxon';
import { IMemoria } from './memoria';
import { TipoEstadoMemoria } from './tipo-estado-memoria';

export interface IEstadoMemoria {
  /** ID */
  id: number;
  /** Memoria */
  memoria: IMemoria;
  /** Tipo estado memoria */
  tipoEstadoMemoria: TipoEstadoMemoria;
  /** Fecha Estado */
  fechaEstado: DateTime;
  /** Comentario */
  comentario: I18nFieldValue[];
}
