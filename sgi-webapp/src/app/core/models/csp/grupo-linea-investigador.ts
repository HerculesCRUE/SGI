import { DateTime } from 'luxon';
import { IPersona } from '../sgp/persona';
import { IGrupoLineaInvestigacion } from './grupo-linea-investigacion';

export interface IGrupoLineaInvestigador {
  id: number;
  persona: IPersona;
  grupoLineaInvestigacion: IGrupoLineaInvestigacion;
  fechaInicio: DateTime;
  fechaFin: DateTime;
}
