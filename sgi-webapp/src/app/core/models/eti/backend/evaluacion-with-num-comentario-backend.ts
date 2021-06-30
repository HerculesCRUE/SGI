import { IEvaluacionBackend } from './evaluacion-backend';

export interface IEvaluacionWithNumComentarioBackend {
  evaluacion: IEvaluacionBackend;
  numComentarios: number;
}
