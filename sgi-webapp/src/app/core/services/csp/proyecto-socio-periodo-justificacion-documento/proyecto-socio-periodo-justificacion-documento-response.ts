import { I18nFieldValueResponse } from '@core/i18n/i18n-field-response';
import { ITipoDocumentoResponse } from '../tipo-documento/tipo-documento-response';

export interface IProyectoSocioPeriodoJustificacionDocumentoResponse {
  id: number;
  proyectoSocioPeriodoJustificacionId: number;
  nombre: I18nFieldValueResponse[];
  documentoRef: string;
  tipoDocumento: ITipoDocumentoResponse;
  comentario: I18nFieldValueResponse[];
  visible: boolean;
}
