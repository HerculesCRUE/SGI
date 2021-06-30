import { IPersona } from '../sgp/persona';
import { IRolProyecto } from './rol-proyecto';

export interface IMiembroEquipoSolicitud {
  id: number;
  persona: IPersona;
  rolProyecto: IRolProyecto;
  mesInicio: number;
  mesFin: number;
}
