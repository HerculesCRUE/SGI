import { IPersona } from '../sgp/persona';
import { IEvaluador } from './evaluador';

export interface IConflictoInteres {
  /** Id */
  id: number;
  /** Evaluador */
  evaluador: IEvaluador;
  /** Referencia persona conflicto */
  personaConflicto: IPersona;
}
