import { IClasificacion } from '../sgo/clasificacion';

export interface ISolicitudProyectoClasificacion {
  id: number;
  solicitudProyectoId: number;
  clasificacion: IClasificacion;
}
