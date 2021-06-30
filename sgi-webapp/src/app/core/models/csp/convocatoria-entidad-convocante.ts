import { IEmpresa } from '../sgemp/empresa';
import { IPrograma } from './programa';

export interface IConvocatoriaEntidadConvocante {
  id: number;
  convocatoriaId: number;
  entidad: IEmpresa;
  programa: IPrograma;
}
