import { IEmpresa } from '../sgemp/empresa';
import { IPrograma } from './programa';

export interface ISolicitudModalidad {
  /** Id */
  id: number;
  /** Id de Solicitud */
  solicitudId: number;
  /** Entidad */
  entidad: IEmpresa;
  /** Programa */
  programa: IPrograma;
}
