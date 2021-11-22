import { Estado } from "../estado-gasto-proyecto";

export interface IEstadoGastoProyectoBackend {
  /** Id */
  id: number;
  /** Id del gasto proyecto */
  gastoProyectoId: number;
  /** Estado */
  estado: Estado;
  /** Fecha estado */
  fechaEstado: string;
  /** Comentario */
  comentario: string;
}
