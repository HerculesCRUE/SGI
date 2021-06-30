import { IConceptoGasto } from '../tipos-configuracion';

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
