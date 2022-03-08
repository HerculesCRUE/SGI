import { Dedicacion } from "@core/models/csp/grupo-equipo";

export interface IGrupoEquipoRequest {
  personaRef: string;
  grupoId: number;
  fechaInicio: string;
  fechaFin: string;
  rolId: number;
  dedicacion: Dedicacion;
  participacion: number;
}
