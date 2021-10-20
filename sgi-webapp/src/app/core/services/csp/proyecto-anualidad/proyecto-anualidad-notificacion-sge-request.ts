export interface IProyectoAnualidadNotificacionSgeRequest {
  anio: number;
  proyectoFechaInicio: string;
  proyectoFechaFin: string;
  totalGastos: number;
  totalIngresos: number;
  proyectoId: number;
  proyectoTitulo: string;
  proyectoAcronimo: string;
  proyectoEstado: string;
  proyectoSgeRef: string;
  enviadoSge: boolean;
}
