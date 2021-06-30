import { IEmpresa } from '../sgemp/empresa';

export interface IProyectoEntidadGestora {
  /** Id */
  id: number;
  /** Id de Proyecto */
  proyectoId: number;
  /** Empresa */
  empresa: IEmpresa;
}
