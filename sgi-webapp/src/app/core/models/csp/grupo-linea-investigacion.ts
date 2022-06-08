import { DateTime } from 'luxon';
import { IGrupo } from './grupo';
import { ILineaInvestigacion } from './linea-investigacion';

export interface IGrupoLineaInvestigacion {
  id: number;
  lineaInvestigacion: ILineaInvestigacion;
  grupo: IGrupo;
  fechaInicio: DateTime;
  fechaFin: DateTime;
}
