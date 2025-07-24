import { I18nFieldValueResponse } from '@core/i18n/i18n-field-response';
import { ITipoDocumentoResponse } from '../tipo-documento/tipo-documento-response';

export interface IDocumentoRequeridoSolicitudResponse {
  id: number;
  configuracionSolicitudId: number;
  tipoDocumento: ITipoDocumentoResponse;
  observaciones: I18nFieldValueResponse[];
}
