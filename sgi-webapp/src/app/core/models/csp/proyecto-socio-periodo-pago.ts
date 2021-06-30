import { DateTime } from 'luxon';

export interface IProyectoSocioPeriodoPago {
  id: number;
  proyectoSocioId: number;
  numPeriodo: number;
  importe: number;
  fechaPrevistaPago: DateTime;
  fechaPago: DateTime;
}
