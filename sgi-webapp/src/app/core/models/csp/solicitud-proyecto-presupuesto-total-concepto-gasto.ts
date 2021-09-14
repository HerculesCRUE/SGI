import { IConceptoGasto } from './concepto-gasto';

export interface ISolicitudProyectoPresupuestoTotalConceptoGasto {
  conceptoGasto: IConceptoGasto;
  importeTotalSolicitado: number;
  importeTotalPresupuestado: number;
}
