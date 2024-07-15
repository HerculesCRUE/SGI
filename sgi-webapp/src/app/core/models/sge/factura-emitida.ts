export interface IFacturaEmitida {
  id: string;
  proyectoId: string;
  anualidad: string;
  numeroFactura: string;
  columnas: {
    [name: string]: string | number;
  };
}
