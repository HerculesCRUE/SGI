import { I18nFieldValueRequest } from "@core/i18n/i18n-field-request";
import { Tipo } from "@core/models/csp/grupo-tipo";

export interface IGrupoRequest {
  nombre: I18nFieldValueRequest[];
  fechaInicio: string;
  fechaFin: string;
  proyectoSgeRef: string;
  solicitudId: number;
  codigo: string;
  tipo: Tipo;
  especialInvestigacion: boolean;
  resumen: I18nFieldValueRequest[];
  departamentoOrigenRef: string;
}
