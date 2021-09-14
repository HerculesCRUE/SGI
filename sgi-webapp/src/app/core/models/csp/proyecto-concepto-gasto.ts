import { DateTime } from 'luxon';
import { IConceptoGasto } from './concepto-gasto';

export interface IProyectoConceptoGasto {
  id: number;
  proyectoId: number;
  conceptoGasto: IConceptoGasto;
  permitido: boolean;
  importeMaximo: number;
  fechaInicio: DateTime;
  fechaFin: DateTime;
  observaciones: string;
  convocatoriaConceptoGastoId: number;
}
