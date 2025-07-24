import { I18nFieldValueResponse } from "@core/i18n/i18n-field-response";
import { TipoEstadoValidacion } from "@core/models/csp/estado-validacion-ip";

export interface IEstadoValidacionIPResponse {
  id: number;
  comentario: I18nFieldValueResponse[];
  estado: TipoEstadoValidacion;
  fecha: string;
  proyectoFacturacionId: number;
}
