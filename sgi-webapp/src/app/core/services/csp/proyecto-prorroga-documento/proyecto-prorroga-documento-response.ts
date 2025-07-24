import { I18nFieldValueResponse } from '@core/i18n/i18n-field-response';
import { ITipoDocumentoResponse } from '../tipo-documento/tipo-documento-response';

export interface IProyectoProrrogaDocumentoResponse {
  id: number;
  proyectoProrrogaId: number;
  nombre: I18nFieldValueResponse[];
  documentoRef: string;
  tipoDocumento: ITipoDocumentoResponse;
  visible: boolean;
  comentario: I18nFieldValueResponse[];
}
