import { Dedicacion } from '@core/models/csp/grupo-equipo';

export interface IGrupoEquipoResponse {
  id: number;
  personaRef: string;
  grupoId: number;
  fechaInicio: string;
  fechaFin: string;
  rol: {
    id: number;
    nombre: string;
  };
  dedicacion: Dedicacion;
  participacion: number;
}
