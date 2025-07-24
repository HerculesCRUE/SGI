import { I18nFieldValueResponse } from "@core/i18n/i18n-field-response";
import { ITipoEnlaceResponse } from "@core/services/csp/tipo-enlace/tipo-enlace-response";

export interface IConvocatoriaEnlaceResponse {
  /** id */
  id: number;
  /** tipoEnlace */
  tipoEnlace: ITipoEnlaceResponse;
  /** activo */
  activo: boolean;
  /** Id de Convocatoria */
  convocatoriaId: number;
  /** URL */
  url: string;
  /** descripcion */
  descripcion: I18nFieldValueResponse[];
}
