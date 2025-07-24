import { IEquipoTrabajoResponse } from './equipo-trabajo-response';

export interface IEquipoTrabajoWithIsEliminableResponse extends IEquipoTrabajoResponse {
  /** Eliminable */
  eliminable: boolean;
}
