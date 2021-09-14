import { IProyectoSge } from '../sge/proyecto-sge';
import { IProyecto } from './proyecto';

export interface IProyectoProyectoSge {
  id: number;
  proyecto: IProyecto;
  proyectoSge: IProyectoSge;
}
