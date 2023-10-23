import { TipoEstadoMemoria } from '../../../models/eti/tipo-estado-memoria';
import { IMemoriaBackend } from '../../../models/eti/backend/memoria-backend';

export interface IEstadoMemoriaRequest {
  id: number;
  memoria: IMemoriaBackend;
  tipoEstadoMemoria: TipoEstadoMemoria;
  fechaEstado: string;
  comentario: string;
}
