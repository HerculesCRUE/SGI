export interface IFacturaEmitidaResponse {
  id: string;
  proyectoId: string;
  anualidad: string;
  numeroFactura: string;
  columnas: {
    [name: string]: string | number;
  };
}
