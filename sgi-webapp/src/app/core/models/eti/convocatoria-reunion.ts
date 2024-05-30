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
  lugar: string;
  /** Orden día */
  ordenDia: string;
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
