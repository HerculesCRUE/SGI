import { Estado } from '../estado-proyecto';

export interface IEstadoProyectoBackend {
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
