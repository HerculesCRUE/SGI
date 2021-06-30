import { TipoEstadoMemoria } from '../tipo-estado-memoria';
import { IMemoriaBackend } from './memoria-backend';

export interface IEstadoMemoriaBackend {
  /** ID */
  id: number;
  /** Memoria */
  memoria: IMemoriaBackend;
  /** Tipo estado memoria */
  tipoEstadoMemoria: TipoEstadoMemoria;
  /** Fecha Estado */
  fechaEstado: string;
}
