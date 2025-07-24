import { ITareaResponse } from './tarea-response';

export interface ITareaWithIsEliminableResponse extends ITareaResponse {
  /** Eliminable */
  eliminable: boolean;
}
