import { IEmpresa } from '../sgemp/empresa';
import { IClasificacion } from '../sgo/clasificacion';

export interface ISolicitudRrhh {
  id: number;
  universidad: IEmpresa;
  areaAnep: IClasificacion;
  universidadDatos: string;
}
