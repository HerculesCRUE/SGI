import { DateTime } from 'luxon';
import { ITipoFase } from './tipos-configuracion';

export interface IConvocatoriaFase {
  id: number;
  convocatoriaId: number;
  tipoFase: ITipoFase;
  fechaInicio: DateTime;
  fechaFin: DateTime;
  observaciones: string;
}
