import { IRolProyectoResponse } from "@core/services/csp/rol-proyecto/rol-proyecto-response";

export interface ISolicitudProyectoEquipoResponse {
  id: number;
  solicitudProyectoId: number;
  personaRef: string;
  rolProyecto: IRolProyectoResponse;
  mesInicio: number;
  mesFin: number;
}
