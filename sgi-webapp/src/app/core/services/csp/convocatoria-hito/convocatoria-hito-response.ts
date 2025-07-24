import { ITipoHito } from '@core/models/csp/tipos-configuracion';
import { ITipoHitoResponse } from '../tipo-hito/tipo-hito-response';
import { I18nFieldValueResponse } from '@core/i18n/i18n-field-response';

export interface IConvocatoriaHitoResponse {
  /** Id */
  id: number;
  /** Fecha inicio  */
  fecha: string;
  /** Tipo de hito */
  tipoHito: ITipoHitoResponse;
  /** Comentario */
  comentario: I18nFieldValueResponse[];
  /** Id de Convocatoria */
  convocatoriaId: number;
  aviso: {
    comunicadoRef: string;
    tareaProgramadaRef: string;
    incluirIpsSolicitud: boolean;
    incluirIpsProyecto: boolean;
  };
}
