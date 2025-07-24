import { I18nFieldValueResponse } from '@core/i18n/i18n-field-response';
import { ITipoDocumentoResponse } from '../tipo-documento/tipo-documento-response';
import { ITipoFaseResponse } from '../tipo-fase/tipo-fase-response';

export interface IConvocatoriaDocumentoResponse {
  id: number;
  convocatoriaId: number;
  nombre: I18nFieldValueResponse[];
  documentoRef: string;
  tipoFase: ITipoFaseResponse;
  tipoDocumento: ITipoDocumentoResponse;
  publico: boolean;
  observaciones: I18nFieldValueResponse[];
}
