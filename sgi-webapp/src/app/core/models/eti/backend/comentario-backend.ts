import { IApartado } from '../apartado';
import { TipoEstadoComentario } from '../comentario';
import { TipoComentario } from '../tipo-comentario';
import { IAuditoriaBackend } from './auditoria-backend';
import { IEvaluacionBackend } from './evaluacion-backend';
import { IMemoriaBackend } from './memoria-backend';

export interface IComentarioBackend extends IAuditoriaBackend {
  /** Id */
  id: number;
  /** Memoria */
  memoria: IMemoriaBackend;
  /** Apartado del formulario */
  apartado: IApartado;
  /** Evaluación */
  evaluacion: IEvaluacionBackend;
  /** Tipo de comentario */
  tipoComentario: TipoComentario;
  /** Texto */
  texto: string;
  /** Estado */
  estado: TipoEstadoComentario;
  /** Fecha estado */
  fechaEstado: string;
}
