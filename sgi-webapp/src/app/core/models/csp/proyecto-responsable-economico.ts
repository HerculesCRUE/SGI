import { DateTime } from 'luxon';
import { IPersona } from '../sgp/persona';

export interface IProyectoResponsableEconomico {
  id: number;
  proyectoId: number;
  persona: IPersona;
  fechaInicio: DateTime;
  fechaFin: DateTime;
}
