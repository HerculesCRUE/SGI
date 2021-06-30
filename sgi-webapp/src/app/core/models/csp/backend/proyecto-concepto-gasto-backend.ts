import { IConceptoGasto } from '../tipos-configuracion';

export interface IProyectoConceptoGastoBackend {
  id: number;
  proyectoId: number;
  conceptoGasto: IConceptoGasto;
  permitido: boolean;
  importeMaximo: number;
  fechaInicio: string;
  fechaFin: string;
  observaciones: string;
  porcentajeCosteIndirecto: number;
  convocatoriaConceptoGastoId: number;
}
