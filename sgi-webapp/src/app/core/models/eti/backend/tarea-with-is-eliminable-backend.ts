import { ITareaBackend } from './tarea-backend';

export interface ITareaWithIsEliminableBackend extends ITareaBackend {
  /** Eliminable */
  eliminable: boolean;
}
