import { I18nFieldValueRequest } from "@core/i18n/i18n-field-request";
import { Estado } from "../../../models/csp/estado-gasto-proyecto";

export interface IEstadoGastoProyectoRequest {
  /** Id */
  id: number;
  /** Id del gasto proyecto */
  gastoProyectoId: number;
  /** Estado */
  estado: Estado;
  /** Fecha estado */
  fechaEstado: string;
  /** Comentario */
  comentario: I18nFieldValueRequest[];
}
