import { I18nFieldValueResponse } from "@core/i18n/i18n-field-response";
import { TipoEntidad } from "@core/models/csp/relacion-ejecucion-economica";

export interface IRelacionEjecucionEconomicaResponse {
  id: number;
  nombre: I18nFieldValueResponse[];
  codigoExterno?: string;
  codigoInterno?: string;
  fechaInicio: string;
  fechaFin: string;
  proyectoSgeRef: string;
  tipoEntidad: TipoEntidad;
  fechaFinDefinitiva: string;
}