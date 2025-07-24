import { I18nFieldValue } from '@core/i18n/i18n-field';
import { DateTime } from 'luxon';
import { IComite } from './comite';
import { TipoConvocatoriaReunion } from './tipo-convocatoria-reunion';

export interface IConvocatoriaReunion {
  /** ID */
  id: number;
  /** Comite */
  comite: IComite;
  /** Tipo Convocatoria Reunion */
  tipoConvocatoriaReunion: TipoConvocatoriaReunion;
  /** Fecha evaluación */
  fechaEvaluacion: DateTime;
  /** Hora fin */
  horaInicio: number;
  /** Minuto inicio */
  minutoInicio: number;
  /** Hora inicio Segunda */
  horaInicioSegunda: number;
  /** Minuto inicio Segunda */
  minutoInicioSegunda: number;
  /** Fecha Limite */
  fechaLimite: DateTime;
  /** Videoconferencia */
  videoconferencia: boolean;
  /** Lugar */
  lugar: I18nFieldValue[];
  /** Orden día */
  ordenDia: I18nFieldValue[];
  /** Año */
  anio: number;
  /** Numero acta */
  numeroActa: number;
  /** Fecha Envío */
  fechaEnvio: DateTime;
  /** Activo */
  activo: boolean;
  /** Código */
  codigo: string;
}
