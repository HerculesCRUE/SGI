import { I18nFieldValueResponse } from '@core/i18n/i18n-field-response';


export interface IDocumentacionConvocatoriaReunionResponse {
  /** Id */
  id: number;
  /** Convocatoria Reunion */
  convocatoriaReunionId: number;
  /** Nombre */
  nombre: I18nFieldValueResponse[];
  /** Ref del documento */
  documentoRef: string;
}
