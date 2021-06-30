import { IPeticionEvaluacionBackend } from './peticion-evaluacion-backend';

export interface IPeticionEvaluacionWithIsEliminableBackend extends IPeticionEvaluacionBackend {
  /** Eliminable */
  eliminable: boolean;
}
