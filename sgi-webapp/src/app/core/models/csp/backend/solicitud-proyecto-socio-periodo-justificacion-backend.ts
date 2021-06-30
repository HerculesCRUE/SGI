export interface ISolicitudProyectoSocioPeriodoJustificacionBackend {
  id: number;
  solicitudProyectoSocioId: number;
  numPeriodo: number;
  mesInicial: number;
  mesFinal: number;
  fechaInicio: string;
  fechaFin: string;
  observaciones: string;
}
