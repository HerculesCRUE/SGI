import { IComite } from '../comite';
import { TipoConvocatoriaReunion } from '../tipo-convocatoria-reunion';

export interface IConvocatoriaReunionBackend {
  /** ID */
  id: number;
  /** Comite */
  comite: IComite;
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
  lugar: string;
  /** Orden día */
  ordenDia: string;
  /** Año */
  anio: number;
  /** Numero acta */
  numeroActa: number;
  /** Fecha Envío */
  fechaEnvio: string;
  /** Activo */
  activo: boolean;
}
