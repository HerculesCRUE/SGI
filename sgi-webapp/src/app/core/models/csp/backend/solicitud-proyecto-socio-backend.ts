import { IRolSocio } from '../rol-socio';

export interface ISolicitudProyectoSocioBackend {
  id: number;
  solicitudProyectoId: number;
  empresaRef: string;
  rolSocio: IRolSocio;
  mesInicio: number;
  mesFin: number;
  numInvestigadores: number;
  importeSolicitado: number;
  importePresupuestado: number;
}
