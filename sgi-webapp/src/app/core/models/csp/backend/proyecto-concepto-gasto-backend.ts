import { IConceptoGasto } from '../concepto-gasto';

export interface IProyectoConceptoGastoBackend {
  id: number;
  proyectoId: number;
  conceptoGasto: IConceptoGasto;
  permitido: boolean;
  importeMaximo: number;
  fechaInicio: string;
  fechaFin: string;
  observaciones: string;
  convocatoriaConceptoGastoId: number;
}
