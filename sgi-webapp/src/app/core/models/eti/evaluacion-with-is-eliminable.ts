import { IEvaluacion } from './evaluacion';

export interface IEvaluacionWithIsEliminable extends IEvaluacion {
  /** Flag eliminable */
  eliminable: boolean;
}
