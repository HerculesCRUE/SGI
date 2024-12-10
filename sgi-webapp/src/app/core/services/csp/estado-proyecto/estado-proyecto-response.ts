import { Estado } from '../../../models/csp/estado-proyecto';

export interface IEstadoProyectoResponse {
  /** Id */
  id: number;
  /** Id del proyecto */
  proyectoId: number;
  /** Estado */
  estado: Estado;
  /** Fecha estado */
  fechaEstado: string;
  /** Comentario */
  comentario: string;
}
