import { IEquipoTrabajoBackend } from './equipo-trabajo-backend';

export interface IEquipoTrabajoWithIsEliminableBackend extends IEquipoTrabajoBackend {
  /** Eliminable */
  eliminable: boolean;
}
