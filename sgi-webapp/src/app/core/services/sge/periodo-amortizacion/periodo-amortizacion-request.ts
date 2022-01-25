export interface IPeriodoAmortizacionRequest {
  id: string;
  proyectoId: string;
  anualidad: string;
  empresaRef: string;
  tipoFinanciacion: {
    id: string,
    nombre: string
  },
  fuenteFinanciacion: {
    id: string,
    nombre: string
  },
  fecha: string;
  importe: number;
}