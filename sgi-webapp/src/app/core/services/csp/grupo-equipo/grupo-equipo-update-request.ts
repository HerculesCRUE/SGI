import { Dedicacion } from '@core/models/csp/grupo-equipo';

export interface IGrupoEquipoUpdateRequest {
  id: number;
  personaRef: string;
  grupoId: number;
  fechaInicio: string;
  fechaFin: string;
  rolId: number;
  dedicacion: Dedicacion;
  participacion: number;
}
