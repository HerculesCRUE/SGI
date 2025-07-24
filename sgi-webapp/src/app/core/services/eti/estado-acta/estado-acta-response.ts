import { TipoEstadoActa } from '../../../models/eti/tipo-estado-acta';
import { IActaResponse } from '../acta/acta-response';

export interface IEstadoActaResponse {
  /** ID */
  id: number;
  /** Acta */
  acta: IActaResponse;
  /** Tipo estado acta */
  tipoEstadoActa: TipoEstadoActa;
  /** Fecha Estado */
  fechaEstado: string;
}
