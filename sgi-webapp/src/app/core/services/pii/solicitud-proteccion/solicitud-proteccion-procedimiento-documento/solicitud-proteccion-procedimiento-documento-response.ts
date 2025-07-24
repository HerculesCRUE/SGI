import { I18nFieldValueResponse } from "@core/i18n/i18n-field-response";

export interface IProcedimientoDocumentoResponse {

  id: number;
  nombre: I18nFieldValueResponse[];
  documentoRef: string;
  procedimientoId: number;

}
