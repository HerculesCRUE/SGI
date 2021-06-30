export interface IProyectoSocioPeriodoJustificacionBackend {
  id: number;
  proyectoSocioId: number;
  numPeriodo: number;
  fechaInicio: string;
  fechaFin: string;
  fechaInicioPresentacion: string;
  fechaFinPresentacion: string;
  observaciones: string;
  documentacionRecibida: boolean;
  fechaRecepcion: string;
  importeJustificado: number;
}
