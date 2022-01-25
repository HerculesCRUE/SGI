import { IProyecto } from './proyecto';

export interface IProyectoPalabraClave {
  id: number;
  proyecto: IProyecto;
  palabraClave: string;
}
