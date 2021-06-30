import { IPeticionEvaluacionBackend } from './peticion-evaluacion-backend';

export interface IEquipoTrabajoBackend {
  /** ID */
  id: number;
  /** Persona ref */
  personaRef: string;
  /** Peticion evaluación */
  peticionEvaluacion: IPeticionEvaluacionBackend;
}
