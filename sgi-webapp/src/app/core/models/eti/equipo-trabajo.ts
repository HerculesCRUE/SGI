import { IPersona } from '../sgp/persona';

export interface IEquipoTrabajo {
  /** ID */
  id: number;
  /** Persona */
  persona: IPersona;
  /** Peticion evaluación Id */
  peticionEvaluacionId: number;
}
