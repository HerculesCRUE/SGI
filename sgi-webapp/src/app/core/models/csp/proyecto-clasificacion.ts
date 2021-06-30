import { IClasificacion } from '../sgo/clasificacion';

export interface IProyectoClasificacion {
  id: number;
  proyectoId: number;
  clasificacion: IClasificacion;
}
