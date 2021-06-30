import { IConvocatoriaReunionBackend } from './convocatoria-reunion-backend';
import { IEvaluadorBackend } from './evaluador-backend';

export interface IAsistenteBackend {
  /** Id */
  id: number;
  /** Evaluador. */
  evaluador: IEvaluadorBackend;
  /** Convocatoria de la reunión */
  convocatoriaReunion: IConvocatoriaReunionBackend;
  /** Asistencia */
  asistencia: boolean;
  /** Motivo */
  motivo: string;
}
