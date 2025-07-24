import { I18nFieldValue } from '@core/i18n/i18n-field';
import { IFormulario } from './formulario';

export interface IBloque {
  /** Id */
  id: number;

  /** Formulario */
  formulario: IFormulario;

  /** orden */
  orden: number;

  /** bloque language */
  nombre: I18nFieldValue[];
}
