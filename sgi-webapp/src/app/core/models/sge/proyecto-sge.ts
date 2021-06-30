import { DateTime } from 'luxon';

export interface IProyectoSge {
  id: string;
  titulo: string;
  fechaInicio: DateTime;
  fechaFin: DateTime;
  proyectoSGIId: string;
  sectorIva: boolean;
}
