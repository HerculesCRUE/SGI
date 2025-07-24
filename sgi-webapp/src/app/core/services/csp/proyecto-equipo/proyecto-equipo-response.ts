import { IRolProyectoResponse } from '@core/services/csp/rol-proyecto/rol-proyecto-response';

export interface IProyectoEquipoResponse {
  id: number;
  proyectoId: number;
  rolProyecto: IRolProyectoResponse;
  personaRef: string;
  fechaInicio: string;
  fechaFin: string;
}
