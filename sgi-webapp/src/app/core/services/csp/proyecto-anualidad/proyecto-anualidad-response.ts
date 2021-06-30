
export interface IProyectoAnualidadResponse {
  /** Id */
  id: number;
  /** Año */
  anio: number;
  /** Proyecto */
  proyectoId: number;
  /** Fecha inicio */
  fechaInicio: string;
  /** Fecha fin */
  fechaFin: string;
  /** Presupuestar */
  presupuestar: boolean;
  /** enviadoSge */
  enviadoSge: boolean;
}
