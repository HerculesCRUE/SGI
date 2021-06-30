import { ITarea } from './tarea';

export interface ITareaWithIsEliminable extends ITarea {
  /** Eliminable */
  eliminable: boolean;
}
