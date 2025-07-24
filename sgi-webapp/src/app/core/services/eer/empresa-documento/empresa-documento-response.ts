import { I18nFieldValueResponse } from "@core/i18n/i18n-field-response";
import { ITipoDocumentoResponse } from "@core/services/eer/tipo-documento/tipo-documento-response";

export interface IEmpresaDocumentoResponse {
  id: number;
  nombre: I18nFieldValueResponse[];
  documentoRef: string;
  comentarios: I18nFieldValueResponse[];
  empresaId: number;
  tipoDocumento: ITipoDocumentoResponse;
}
