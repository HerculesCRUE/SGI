import { IPersona } from '../sgp/persona';
import { IApartado } from './apartado';
import { IEvaluacion } from './evaluacion';
import { IMemoria } from './memoria';
import { TipoComentario } from './tipo-comentario';

export interface IComentario {
  /** Id */
  id: number;
  /** Memoria */
  memoria: IMemoria;
  /** Apartado del formulario */
  apartado: IApartado;
  /** Evaluación */
  evaluacion: IEvaluacion;
  /** Tipo de comentario */
  tipoComentario: TipoComentario;
  /** Texto */
  texto: string;
  /** Persona creación/modificación */
  evaluador: IPersona;
}
