import { DateTime } from 'luxon';

export interface ICodigoEconomicoIngreso {
  id: string;
  nombre: string;
  fechaInicio: DateTime;
  fechaFin: DateTime;
}
