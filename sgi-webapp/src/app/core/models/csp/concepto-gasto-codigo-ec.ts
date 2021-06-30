import { DateTime } from 'luxon';

export interface IConceptoGastoCodigoEc {
  id: number;
  codigoEconomicoRef: string;
  fechaInicio: DateTime;
  fechaFin: DateTime;
  observaciones: string;
}
