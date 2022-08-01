import { IConvocatoriaFaseAviso } from '@core/services/csp/convocatoria-fase/convocatoria-fase-aviso';
import { DateTime } from 'luxon';
import { ITipoFase } from './tipos-configuracion';

export interface IConvocatoriaFase {
  id: number;
  convocatoriaId: number;
  tipoFase: ITipoFase;
  fechaInicio: DateTime;
  fechaFin: DateTime;
  observaciones: string;
  aviso1: IConvocatoriaFaseAviso;
  aviso2: IConvocatoriaFaseAviso;
}
