import { IEvaluadorResponse } from '../evaluador/evaluador-response';

export interface IConflictoInteresResponse {
  /** Id */
  id: number;
  /** Evaluador */
  evaluador: IEvaluadorResponse;
  /** Referencia persona conflicto */
  personaConflictoRef: string;
}
