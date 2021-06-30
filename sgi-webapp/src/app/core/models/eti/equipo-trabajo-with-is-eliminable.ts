import { IEquipoTrabajo } from './equipo-trabajo';

export interface IEquipoTrabajoWithIsEliminable extends IEquipoTrabajo {
  /** Eliminable */
  eliminable: boolean;
}
