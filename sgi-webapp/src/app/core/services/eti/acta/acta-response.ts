import { I18nFieldValueResponse } from '@core/i18n/i18n-field-response';
import { TipoEstadoActa } from '../../../models/eti/tipo-estado-acta';
import { IConvocatoriaReunionResponse } from '../convocatoria-reunion/convocatoria-reunion-response';

export interface IActaResponse {
  /** Id */
  id: number;
  /** Convocatoria de la reunión */
  convocatoriaReunion: IConvocatoriaReunionResponse;
  /** Hora inicio */
  horaInicio: number;
  /** Minuto inicio */
  minutoInicio: number;
  /** Hora fin */
  horaFin: number;
  /** Minuto fin */
  minutoFin: number;
  /** Resumen */
  resumen: I18nFieldValueResponse[];
  /** Numero */
  numero: number;
  /** Inactiva */
  inactiva: boolean;
  /** Activo */
  activo: boolean;
  /** Estado Actual */
  estadoActual: TipoEstadoActa;
}
