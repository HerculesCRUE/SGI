

export interface IAnualidadIngresoResponse {
  id: number;
  proyectoAnualidadId: number;
  codigoEconomicoRef: string;
  proyectoPartida: {
    id: number;
    codigo: string;
  };
  importeConcedido: number;
  proyectoSgeRef: string;
}
