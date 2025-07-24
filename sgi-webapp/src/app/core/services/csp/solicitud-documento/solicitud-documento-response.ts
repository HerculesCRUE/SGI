import { I18nFieldValueResponse } from '@core/i18n/i18n-field-response';
import { ITipoDocumentoResponse } from '../tipo-documento/tipo-documento-response';
import { ITipoFaseResponse } from '../tipo-fase/tipo-fase-response';

export interface ISolicitudDocumentoResponse {
  id: number;
  solicitudId: number;
  comentario: I18nFieldValueResponse[];
  documentoRef: string;
  nombre: I18nFieldValueResponse[];
  tipoDocumento: ITipoDocumentoResponse;
  tipoFase: ITipoFaseResponse;
}
