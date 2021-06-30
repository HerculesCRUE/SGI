

export interface IAnualidadGastoResponse {
  id: number;
  proyectoAnualidadId: number;
  conceptoGasto: {
    id: number;
    nombre: string;
  };
  codigoEconomicoRef: string;
  proyectoPartida: {
    id: number;
    codigo: string;
  };
  importePresupuesto: number;
  importeConcedido: number;
  proyectoSgeRef: string;
}
