import { DateTime } from 'luxon';
import { ICodigoEconomicoGasto } from '../sge/codigo-economico-gasto';

export interface IConceptoGastoCodigoEc {
  id: number;
  codigoEconomico: ICodigoEconomicoGasto;
  fechaInicio: DateTime;
  fechaFin: DateTime;
  observaciones: string;
}
