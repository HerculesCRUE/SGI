import { DateTime } from 'luxon';
import { IActa } from './acta';
import { TipoEstadoActa } from './tipo-estado-acta';

export interface IEstadoActa {
  /** ID */
  id: number;
  /** Acta */
  acta: IActa;
  /** Tipo estado acta */
  tipoEstadoActa: TipoEstadoActa;
  /** Fecha Estado */
  fechaEstado: DateTime;
}
