import { DateTime } from 'luxon';
import { IPersona } from '../sgp/persona';
import { IGrupo } from './grupo';

export interface IGrupoPersonaAutorizada {
  id: number;
  persona: IPersona;
  grupo: IGrupo;
  fechaInicio: DateTime;
  fechaFin: DateTime;
}
