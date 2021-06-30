import { DateTime } from 'luxon';

export interface ICodigoEconomicoGasto {
  id: string;
  nombre: string;
  fechaInicio: DateTime;
  fechaFin: DateTime;
}
