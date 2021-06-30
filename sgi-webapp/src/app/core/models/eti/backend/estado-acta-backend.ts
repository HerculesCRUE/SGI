import { TipoEstadoActa } from '../tipo-estado-acta';
import { IActaBackend } from './acta-backend';

export interface IEstadoActaBackend {
  /** ID */
  id: number;
  /** Acta */
  acta: IActaBackend;
  /** Tipo estado acta */
  tipoEstadoActa: TipoEstadoActa;
  /** Fecha Estado */
  fechaEstado: string;
}
