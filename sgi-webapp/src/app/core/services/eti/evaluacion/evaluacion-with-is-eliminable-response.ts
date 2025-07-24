import { IEvaluacionResponse } from './evaluacion-response';

export interface IEvaluacionWithIsEliminableResponse extends IEvaluacionResponse {
  /** Flag eliminable */
  eliminable: boolean;
}
