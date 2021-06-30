import { IFormulario } from './formulario';

export interface IBloque {
  /** Id */
  id: number;

  /** Formulario */
  formulario: IFormulario;

  /** Nombre */
  nombre: string;

  /** Orden */
  orden: number;
}
