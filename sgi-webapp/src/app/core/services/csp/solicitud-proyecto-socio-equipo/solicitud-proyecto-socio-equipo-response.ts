import { IRolProyectoResponse } from '../rol-proyecto/rol-proyecto-response';

export interface ISolicitudProyectoSocioEquipoResponse {
  id: number;
  solicitudProyectoSocioId: number;
  personaRef: string;
  rolProyecto: IRolProyectoResponse;
  mesInicio: number;
  mesFin: number;
}
