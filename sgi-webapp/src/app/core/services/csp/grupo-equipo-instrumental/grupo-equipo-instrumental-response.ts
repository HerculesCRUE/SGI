import { Dedicacion } from "@core/models/csp/grupo-equipo";

export interface IGrupoEquipoInstrumentalResponse {
  id: number;
  grupoId: number;
  nombre: string;
  numRegistro: string;
  descripcion: string;
}
