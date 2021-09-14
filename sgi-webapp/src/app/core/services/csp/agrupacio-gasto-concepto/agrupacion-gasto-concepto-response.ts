export interface IAgrupacionGastoConceptoResponse {
  id: number;
  agrupacionId: number;
  conceptoGasto: {
    id: number;
    nombre: string;
  };
}
