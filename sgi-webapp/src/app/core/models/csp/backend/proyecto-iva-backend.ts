export interface IProyectoIVABackend {
  /** Id */
  id: number;
  /** Id del proyecto */
  proyectoId: number;
  /** Porcentaje de IVA */
  iva: number;
  /** Fecha de inicio */
  fechaInicio: string;
  /** Fecha de Fin */
  fechaFin: string;
}