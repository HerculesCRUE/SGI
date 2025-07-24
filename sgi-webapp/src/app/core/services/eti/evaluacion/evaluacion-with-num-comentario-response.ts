import { IEvaluacionResponse } from './evaluacion-response';

export interface IEvaluacionWithNumComentarioResponse {
  evaluacion: IEvaluacionResponse;
  numComentarios: number;
}
