import { IEstadoGastoProyecto } from "@core/models/csp/estado-gasto-proyecto";

export interface IGastoProyectoRequest {
  proyectoId: number;
  gastoRef: string;
  conceptoGastoId: number;
  estado: IEstadoGastoProyecto;
  fechaCongreso: string;
  importeInscripcion: number;
  observaciones: string;
}
