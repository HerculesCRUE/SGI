import { I18nFieldValueResponse } from '@core/i18n/i18n-field-response';
import { ITipoDocumentoResponse } from '../tipo-documento/tipo-documento-response';
import { IMemoriaResponse } from './memoria-response';

export interface IDocumentacionMemoriaResponse {
  /** Id */
  id: number;
  /** Memoria */
  memoria: IMemoriaResponse;
  /** TIpo de documento */
  tipoDocumento: ITipoDocumentoResponse;
  /** Nombre */
  nombre: I18nFieldValueResponse[];
  /** Ref del documento */
  documentoRef: string;
}
