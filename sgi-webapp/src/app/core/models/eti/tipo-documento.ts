import { I18nFieldValue } from '@core/i18n/i18n-field';

export interface ITipoDocumento {
  /** Id */
  id: number;
  /** Codigo */
  codigo: string;
  /** Nombre */
  nombre: I18nFieldValue[];
  /** Formulario Id */
  formularioId: number;
  /** Documentación adicional */
  adicional: boolean;
  /** Activo */
  activo: boolean;
}
