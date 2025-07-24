import { I18nFieldValueResponse } from '@core/i18n/i18n-field-response';
import { ITipoHitoResponse } from '../tipo-hito/tipo-hito-response';

export interface ISolicitudHitoResponse {
  /** Id */
  id: number;
  /** Fecha inicio  */
  fecha: string;
  /** Tipo de hito */
  tipoHito: ITipoHitoResponse;
  /** Comentario */
  comentario: I18nFieldValueResponse[];
  /** Id de Solicitud */
  solicitudId: number;
  createdBy: string;
  aviso: {
    comunicadoRef: string;
    tareaProgramadaRef: string;
    incluirIpsSolicitud: boolean;
  };
}
