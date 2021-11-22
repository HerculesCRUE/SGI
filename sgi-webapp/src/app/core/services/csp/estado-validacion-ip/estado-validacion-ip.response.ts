import { TipoEstadoValidacion } from "@core/models/csp/estado-validacion-ip";
import { DateTime } from "luxon";

export interface IEstadoValidacionIPResponse {
  id: number;
  comentario: string;
  estado: TipoEstadoValidacion;
  fecha: string;
  proyectoFacturacionId: number;
}
