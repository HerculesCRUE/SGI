import { TipoEntidad } from "@core/models/csp/relacion-ejecucion-economica";

export interface IRelacionEjecucionEconomicaResponse {
  id: number;
  nombre: string;
  codigoExterno?: string;
  codigoInterno?: string;
  fechaInicio: string;
  fechaFin: string;
  proyectoSgeRef: string;
  tipoEntidad: TipoEntidad;
}