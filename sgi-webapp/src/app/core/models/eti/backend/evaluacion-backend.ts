import { IComite } from '../comite';
import { IDictamen } from '../dictamen';
import { TipoEvaluacion } from '../tipo-evaluacion';
import { IConvocatoriaReunionBackend } from './convocatoria-reunion-backend';
import { IEvaluadorBackend } from './evaluador-backend';
import { IMemoriaBackend } from './memoria-backend';

export interface IEvaluacionBackend {
  /** ID */
  id: number;
  /** Memoria */
  memoria: IMemoriaBackend;
  /** Comite */
  comite: IComite;
  /** Convocatoria reunión */
  convocatoriaReunion: IConvocatoriaReunionBackend;
  /** Tipo evaluación */
  tipoEvaluacion: TipoEvaluacion;
  /** Version */
  version: number;
  /** Dictamen */
  dictamen: IDictamen;
  /** Evaluador 1 */
  evaluador1: IEvaluadorBackend;
  /** Evaluador 2 */
  evaluador2: IEvaluadorBackend;
  /** Fecha Inicio. */
  fechaDictamen: string;
  /** Es revisión mínima */
  esRevMinima: boolean;
  /** Comentario */
  comentario: string;
  /** Activo */
  activo: boolean;
}
