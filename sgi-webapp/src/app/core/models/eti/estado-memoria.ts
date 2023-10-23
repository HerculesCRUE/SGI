
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
  comentario: string;
}
