import { IRolSocioResponse } from '@core/services/csp/rol-socio/rol-socio-response';

export interface ISolicitudProyectoSocioResponse {
  id: number;
  solicitudProyectoId: number;
  empresaRef: string;
  rolSocio: IRolSocioResponse;
  mesInicio: number;
  mesFin: number;
  numInvestigadores: number;
  importeSolicitado: number;
  importePresupuestado: number;
}
