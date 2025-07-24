import { I18nFieldValueResponse } from '@core/i18n/i18n-field-response';

export interface ITipoDocumentoResponse {
  /** Id */
  id: number;
  /** Codigo */
  codigo: string;
  /** Nombre */
  nombre: I18nFieldValueResponse[];
  /** Formulario Id */
  formularioId: number;
  /** Documentación adicional */
  adicional: boolean;
  /** Activo */
  activo: boolean;
}