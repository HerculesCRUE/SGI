export interface IAnualidadGastoResponse {
  id: number;
  proyectoAnualidadId: number;
  conceptoGasto: {
    id: number;
    nombre: string;
    costesIndirectos: boolean;
  };
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
