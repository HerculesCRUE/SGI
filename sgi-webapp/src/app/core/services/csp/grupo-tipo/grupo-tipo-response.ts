import { Tipo } from "@core/models/csp/grupo-tipo";

export interface IGrupoTipoResponse {
  id: number;
  tipo: Tipo;
  grupoId: number;
  fechaInicio: string;
  fechaFin: string;
}
