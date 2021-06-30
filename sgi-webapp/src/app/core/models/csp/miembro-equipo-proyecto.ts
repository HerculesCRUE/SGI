import { DateTime } from 'luxon';
import { IPersona } from '../sgp/persona';
import { IRolProyecto } from './rol-proyecto';

export interface IMiembroEquipoProyecto {
  id: number;
  rolProyecto: IRolProyecto;
  persona: IPersona;
  fechaInicio: DateTime;
  fechaFin: DateTime;
  horasDedicacion?: number;
}
