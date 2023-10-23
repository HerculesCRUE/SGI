import { IMemoriaBackend } from "@core/models/eti/backend/memoria-backend";
import { TipoEstadoMemoria } from "@core/models/eti/tipo-estado-memoria";

export interface IEstadoMemoriaResponse {
  id: number;
  memoria: IMemoriaBackend;
  tipoEstadoMemoria: TipoEstadoMemoria;
  fechaEstado: string;
  comentario: string;
}
