import { DateTime } from 'luxon';
import { IConceptoGasto } from './concepto-gasto';
import { IEstadoGastoProyecto } from './estado-gasto-proyecto';

export interface IGastoProyecto {
  id: number;
  proyectoId: number;
  gastoRef: string;
  conceptoGasto: IConceptoGasto;
  estado: IEstadoGastoProyecto;
  fechaCongreso: DateTime;
  importeInscripcion: number;
  observaciones: string;
}
