import { IConceptoGasto } from '../concepto-gasto';

export interface ISolicitudProyectoPresupuestoBackend {
  id: number;
  solicitudProyectoId: number;
  conceptoGasto: IConceptoGasto;
  entidadRef: string;
  anualidad: number;
  importeSolicitado: number;
  importePresupuestado: number;
  observaciones: string;
  financiacionAjena: boolean;
}
