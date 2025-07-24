import { IPeticionEvaluacionResponse } from './peticion-evaluacion-response';

export interface IPeticionEvaluacionWithIsEliminableResponse extends IPeticionEvaluacionResponse {
  /** Eliminable */
  eliminable: boolean;
}
