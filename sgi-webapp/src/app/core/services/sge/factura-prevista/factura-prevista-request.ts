
export interface IFacturaPrevistaRequest {
  proyectoIdSGI: number;
  proyectoSgeId: string;
  numeroPrevision: number;
  fechaEmision: string;
  importeBase: number;
  porcentajeIVA: number;
  comentario: string;
  tipoFacturacion: string;
}
