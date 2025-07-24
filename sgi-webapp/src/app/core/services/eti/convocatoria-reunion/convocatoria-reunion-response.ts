import { I18nFieldValueResponse } from '@core/i18n/i18n-field-response';
import { IComiteResponse } from '@core/services/eti/comite/comite-response';
import { TipoConvocatoriaReunion } from '../../../models/eti/tipo-convocatoria-reunion';

export interface IConvocatoriaReunionResponse {
  /** ID */
  id: number;
  /** Comite */
  comite: IComiteResponse;
  /** Tipo Convocatoria Reunion */
  tipoConvocatoriaReunion: TipoConvocatoriaReunion;
  /** Fecha evaluación */
  fechaEvaluacion: string;
  /** Hora fin */
  horaInicio: number;
  /** Minuto inicio */
  minutoInicio: number;
  /** Hora inicio Segunda */
  horaInicioSegunda: number;
  /** Minuto inicio Segunda */
  minutoInicioSegunda: number;
  /** Fecha Limite */
  fechaLimite: string;
  /** Videoconferencia */
  videoconferencia: boolean;
  /** Lugar */
  lugar: I18nFieldValueResponse[];
  /** Orden día */
  ordenDia: I18nFieldValueResponse[];
  /** Año */
  anio: number;
  /** Numero acta */
  numeroActa: number;
  /** Fecha Envío */
  fechaEnvio: string;
  /** Activo */
  activo: boolean;
}
