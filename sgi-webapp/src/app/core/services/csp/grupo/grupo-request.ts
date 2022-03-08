import { Tipo } from "@core/models/csp/grupo-tipo";

export interface IGrupoRequest {
  nombre: string;
  fechaInicio: string;
  fechaFin: string;
  proyectoSgeRef: string;
  solicitudId: number;
  codigo: string;
  tipo: Tipo;
  especialInvestigacion: boolean;
  departamentoOrigenRef: string;
}
