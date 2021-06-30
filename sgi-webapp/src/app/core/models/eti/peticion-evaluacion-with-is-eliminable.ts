import { IPeticionEvaluacion } from './peticion-evaluacion';

export interface IPeticionEvaluacionWithIsEliminable extends IPeticionEvaluacion {
  /** Flag eliminable */
  eliminable: boolean;
}
