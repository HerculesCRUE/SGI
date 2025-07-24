import { I18nFieldValueResponse } from '@core/i18n/i18n-field-response';
import { Estado } from '../../../models/csp/estado-proyecto';

export interface IEstadoProyectoResponse {
  /** Id */
  id: number;
  /** Id del proyecto */
  proyectoId: number;
  /** Estado */
  estado: Estado;
  /** Fecha estado */
  fechaEstado: string;
  /** Comentario */
  comentario: I18nFieldValueResponse[];
}
