import { DateTime } from 'luxon';
import { IGrupo } from './grupo';

export interface IGrupoEspecialInvestigacion {
  id: number;
  especialInvestigacion: boolean;
  fechaInicio: DateTime;
  fechaFin: DateTime;
  grupo: IGrupo;
}
