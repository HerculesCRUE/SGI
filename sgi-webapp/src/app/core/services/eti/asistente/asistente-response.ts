import { I18nFieldValueResponse } from '@core/i18n/i18n-field-response';
import { IConvocatoriaReunionResponse } from '../convocatoria-reunion/convocatoria-reunion-response';
import { IEvaluadorResponse } from '../evaluador/evaluador-response';

export interface IAsistenteResponse {
  /** Id */
  id: number;
  /** Evaluador. */
  evaluador: IEvaluadorResponse;
  /** Convocatoria de la reunión */
  convocatoriaReunion: IConvocatoriaReunionResponse;
  /** Asistencia */
  asistencia: boolean;
  /** Motivo */
  motivo: I18nFieldValueResponse[];
}
