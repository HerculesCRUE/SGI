import { IConvocatoriaReunion } from './convocatoria-reunion';
import { TipoEstadoActa } from './tipo-estado-acta';

export interface IActa {
  /** Id */
  id: number;
  /** Convocatoria de la reunión */
  convocatoriaReunion: IConvocatoriaReunion;
  /** Hora inicio */
  horaInicio: number;
  /** Minuto inicio */
  minutoInicio: number;
  /** Hora fin */
  horaFin: number;
  /** Minuto fin */
  minutoFin: number;
  /** Resumen */
  resumen: string;
  /** Numero */
  numero: number;
  /** Inactiva */
  inactiva: boolean;
  /** Activo */
  activo: boolean;
  /** Estado Actual */
  estadoActual: TipoEstadoActa;
}
