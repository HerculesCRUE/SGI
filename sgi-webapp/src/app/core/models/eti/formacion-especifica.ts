import { I18nFieldValue } from '@core/i18n/i18n-field';

export interface IFormacionEspecifica {
  /** ID */
  id: number;
  /** Nombre */
  nombre: I18nFieldValue[];
  /** Activo */
  activo: boolean;
}
