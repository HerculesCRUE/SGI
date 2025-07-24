import { I18nFieldValueResponse } from "@core/i18n/i18n-field-response";
import { Estado } from "../../../models/csp/estado-gasto-proyecto";

export interface IEstadoGastoProyectoResponse {
  /** Id */
  id: number;
  /** Id del gasto proyecto */
  gastoProyectoId: number;
  /** Estado */
  estado: Estado;
  /** Fecha estado */
  fechaEstado: string;
  /** Comentario */
  comentario: I18nFieldValueResponse[];
}
