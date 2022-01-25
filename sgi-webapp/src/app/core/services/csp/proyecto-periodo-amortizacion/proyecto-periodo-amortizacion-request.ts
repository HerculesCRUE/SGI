export interface IProyectoPeriodoAmortizacionRequest {
  importe: number;
  fechaLimiteAmortizacion: string;
  proyectoSGERef: string;
  proyectoEntidadFinanciadoraId: number;
  proyectoAnualidadId: number
}
