import { DateTime } from 'luxon';
import { IComite } from './comite';
import { IConvocatoriaReunion } from './convocatoria-reunion';
import { IDictamen } from './dictamen';
import { IEvaluador } from './evaluador';
import { IMemoria } from './memoria';
import { TipoEvaluacion } from './tipo-evaluacion';

export interface IEvaluacion {
  /** ID */
  id: number;
  /** Memoria */
  memoria: IMemoria;
  /** Comite */
  comite: IComite;
  /** Convocatoria reunión */
  convocatoriaReunion: IConvocatoriaReunion;
  /** Tipo evaluación */
  tipoEvaluacion: TipoEvaluacion;
  /** Version */
  version: number;
  /** Dictamen */
  dictamen: IDictamen;
  /** Evaluador 1 */
  evaluador1: IEvaluador;
  /** Evaluador 2 */
  evaluador2: IEvaluador;
  /** Fecha Inicio. */
  fechaDictamen: DateTime;
  /** Es revisión mínima */
  esRevMinima: boolean;
  /** Comentario */
  comentario: string;
  /** Activo */
  activo: boolean;
}
