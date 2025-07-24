import { I18nFieldValueResponse } from '@core/i18n/i18n-field-response';

export interface IFormacionEspecificaResponse {
  /** ID */
  id: number;
  /** Nombre */
  nombre: I18nFieldValueResponse[];
  /** Activo */
  activo: boolean;
}
