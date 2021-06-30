import { DateTime } from 'luxon';

export interface ISolicitudProyectoSocioPeriodoJustificacion {
  id: number;
  solicitudProyectoSocioId: number;
  numPeriodo: number;
  mesInicial: number;
  mesFinal: number;
  fechaInicio: DateTime;
  fechaFin: DateTime;
  observaciones: string;
}
