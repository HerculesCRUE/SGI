import { Estado } from '../../../models/csp/estado-proyecto';

export interface IEstadoProyectoRequest {
  /** Id del proyecto */
  proyectoId: number;
  /** Estado */
  estado: Estado;
  /** Fecha estado */
  fechaEstado: string;
  /** Comentario */
  comentario: string;
}
