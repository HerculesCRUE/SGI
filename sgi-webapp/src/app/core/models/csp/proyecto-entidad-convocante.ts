import { IEmpresa } from '../sgemp/empresa';
import { IPrograma } from './programa';

export interface IProyectoEntidadConvocante {
  id: number;
  entidad: IEmpresa;
  programaConvocatoria: IPrograma;
  programa: IPrograma;
}
