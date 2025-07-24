import { IRolProyectoResponse } from '@core/services/csp/rol-proyecto/rol-proyecto-response';

export interface IProyectoSocioEquipoResponse {
  id: number;
  proyectoSocioId: number;
  rolProyecto: IRolProyectoResponse;
  personaRef: string;
  fechaInicio: string;
  fechaFin: string;
}
