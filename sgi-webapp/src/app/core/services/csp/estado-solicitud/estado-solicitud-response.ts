import { I18nFieldValueResponse } from '@core/i18n/i18n-field-response';
import { Estado } from '@core/models/csp/estado-solicitud';


export interface IEstadoSolicitudResponse {
  /** Id */
  id: number;
  /** ID de la solicitud */
  solicitudId: number;
  /** Estado */
  estado: Estado;
  /** Fecha estado */
  fechaEstado: string;
  /** Comentario */
  comentario: I18nFieldValueResponse[];
}
