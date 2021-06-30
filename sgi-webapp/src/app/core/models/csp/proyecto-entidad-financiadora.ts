import { IEntidadFinanciadora } from './entidad-financiadora';

export interface IProyectoEntidadFinanciadora extends IEntidadFinanciadora {
  /**
   * Id del proyecto
   */
  proyectoId: number;
  /**
   * Indica si es ajena a la convocatoria
   */
  ajena: boolean;
}
