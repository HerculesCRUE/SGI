import { IConceptoGastoResponse } from "../concepto-gasto/concepto-gasto-response";
export interface IAgrupacionGastoConceptoResponse {
  id: number;
  agrupacionId: number;
  conceptoGasto: IConceptoGastoResponse;
}
