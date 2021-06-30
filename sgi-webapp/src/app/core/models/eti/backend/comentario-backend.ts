import { IApartado } from '../apartado';
import { TipoComentario } from '../tipo-comentario';
import { IEvaluacionBackend } from './evaluacion-backend';
import { IMemoriaBackend } from './memoria-backend';

export interface IComentarioBackend {
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
}
