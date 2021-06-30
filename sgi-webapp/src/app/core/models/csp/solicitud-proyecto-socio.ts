import { IEmpresa } from '../sgemp/empresa';
import { IRolSocio } from './rol-socio';

export interface ISolicitudProyectoSocio {
  id: number;
  solicitudProyectoId: number;
  empresa: IEmpresa;
  rolSocio: IRolSocio;
  mesInicio: number;
  mesFin: number;
  numInvestigadores: number;
  importeSolicitado: number;
  importePresupuestado: number;
}
