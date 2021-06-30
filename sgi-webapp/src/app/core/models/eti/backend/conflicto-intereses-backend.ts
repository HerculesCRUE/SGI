import { IEvaluadorBackend } from './evaluador-backend';

export interface IConflictoInteresBackend {
  /** Id */
  id: number;
  /** Evaluador */
  evaluador: IEvaluadorBackend;
  /** Referencia persona conflicto */
  personaConflictoRef: string;
}
