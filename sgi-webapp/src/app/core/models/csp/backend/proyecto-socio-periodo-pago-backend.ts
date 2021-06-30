export interface IProyectoSocioPeriodoPagoBackend {
  id: number;
  proyectoSocioId: number;
  numPeriodo: number;
  importe: number;
  fechaPrevistaPago: string;
  fechaPago: string;
}
