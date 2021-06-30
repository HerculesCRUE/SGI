import { IPersona } from '../sgp/persona';
import { IPeticionEvaluacion } from './peticion-evaluacion';

export interface IEquipoTrabajo {
  /** ID */
  id: number;
  /** Persona */
  persona: IPersona;
  /** Peticion evaluación */
  peticionEvaluacion: IPeticionEvaluacion;
}
