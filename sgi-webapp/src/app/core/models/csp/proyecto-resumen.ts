import { DateTime } from 'luxon';

export interface IProyectoResumen {
  id: number;
  titulo: string;
  acronimo: string;
  codigoExterno: string;
  fechaInicio: DateTime;
  fechaFin: DateTime;
  fechaFinDefinitiva: DateTime;
}
