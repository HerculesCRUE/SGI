import { SgiFormlyFieldConfig } from '@formly-forms/formly-field-config';
import { IBloque } from './bloque';

export interface IApartado {
  /** Id */
  id: number;

  /** Bloque del formulario */
  bloque: IBloque;

  /** Nombre */
  nombre: string;

  /** Apartado padre del formulario */
  padre: IApartado;

  /** Orden */
  orden: number;

  /** Esquema */
  esquema: SgiFormlyFieldConfig[];
}
