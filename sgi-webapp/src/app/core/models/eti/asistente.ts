import { IConvocatoriaReunion } from './convocatoria-reunion';
import { IEvaluador } from './evaluador';

export interface IAsistente {
  /** Id */
  id: number;
  /** Evaluador. */
  evaluador: IEvaluador;
  /** Convocatoria de la reunión */
  convocatoriaReunion: IConvocatoriaReunion;
  /** Asistencia */
  asistencia: boolean;
  /** Motivo */
  motivo: string;
}
