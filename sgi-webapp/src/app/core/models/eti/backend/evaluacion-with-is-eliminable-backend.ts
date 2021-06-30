import { IEvaluacionBackend } from './evaluacion-backend';

export interface IEvaluacionWithIsEliminableBackend extends IEvaluacionBackend {
  /** Flag eliminable */
  eliminable: boolean;
}
