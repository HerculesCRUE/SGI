import { I18nFieldValueResponse } from "@core/i18n/i18n-field-response";
import { Tipo } from "@core/models/csp/grupo-tipo";

export interface IGrupoResponse {
  id: number;
  nombre: I18nFieldValueResponse[];
  fechaInicio: string;
  fechaFin: string;
  proyectoSgeRef: string;
  solicitudId: number;
  codigo: string;
  tipo: Tipo;
  especialInvestigacion: boolean;
  resumen: I18nFieldValueResponse[];
  activo: boolean;
}
