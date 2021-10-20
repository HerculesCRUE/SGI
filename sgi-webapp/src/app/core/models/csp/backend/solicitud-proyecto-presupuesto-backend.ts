import { IConceptoGasto } from '../concepto-gasto';

export interface ISolicitudProyectoPresupuestoBackend {
  id: number;
  solicitudProyectoId: number;
  solicitudProyectoEntidadId: number;
  conceptoGasto: IConceptoGasto;
  anualidad: number;
  importeSolicitado: number;
  importePresupuestado: number;
  observaciones: string;
}
