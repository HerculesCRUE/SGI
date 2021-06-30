import { IFormulario } from './formulario';

export interface ITipoDocumento {
  /** Id */
  id: number;

  /** Nombre */
  nombre: string;

  /** Formulario */
  formulario: IFormulario;

  /** Activo */
  activo: boolean;
}
