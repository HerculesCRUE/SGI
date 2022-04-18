import { Dedicacion } from "@core/models/csp/grupo-equipo";

export interface IGrupoEquipoRequest {
  id: number;
  personaRef: string;
  grupoId: number;
  fechaInicio: string;
  fechaFin: string;
  rolId: number;
  dedicacion: Dedicacion;
  participacion: number;
}
