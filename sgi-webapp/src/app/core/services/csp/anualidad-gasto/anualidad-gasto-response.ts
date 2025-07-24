import { IConceptoGastoResponse } from "../concepto-gasto/concepto-gasto-response";

export interface IAnualidadGastoResponse {
  id: number;
  proyectoAnualidadId: number;
  conceptoGasto: IConceptoGastoResponse,
  codigoEconomicoRef: string;
  proyectoPartida: {
    id: number;
    codigo: string;
    partidaRef: string;
  };
  importePresupuesto: number;
  importeConcedido: number;
  proyectoSgeRef: string;
}
