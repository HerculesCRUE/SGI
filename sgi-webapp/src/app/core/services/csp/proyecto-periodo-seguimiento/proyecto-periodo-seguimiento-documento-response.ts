import { I18nFieldValue } from '@core/i18n/i18n-field';
import { ITipoDocumentoResponse } from '../tipo-documento/tipo-documento-response';
import { I18nFieldValueResponse } from '@core/i18n/i18n-field-response';

export interface IProyectoPeriodoSeguimientoDocumentoResponse {
  id: number;
  proyectoPeriodoSeguimientoId: number;
  nombre: I18nFieldValueResponse[];
  documentoRef: string;
  tipoDocumento: ITipoDocumentoResponse;
  visible: boolean;
  comentario: I18nFieldValueResponse[];
}
