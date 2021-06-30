
export interface IProyectoAnualidadResumenResponse {
  /** Id */
  id: number;
  /** Año */
  anio: number;
  /** Fecha inicio */
  fechaInicio: string;
  /** Fecha fin */
  fechaFin: string;
  /** Total gastos presupuesto */
  totalGastosPresupuesto: number;
  /** Total gastos concedido */
  totalGastosConcedido: number;
  /** Total ingresos */
  totalIngresos: number;
  /** Presupuestar */
  presupuestar: boolean;
  /** enviadoSge */
  enviadoSge: boolean;
}
