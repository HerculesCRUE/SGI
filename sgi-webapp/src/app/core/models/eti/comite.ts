import { IFormulario } from './formulario';

export enum COMITE {
  CEISH = 1,
  CEEA = 2,
  CEIAB = 3
}

export interface IComite {
  /** Id. */
  id: number;

  /** Comité */
  comite: string;

  /** Formulario */
  formulario: IFormulario;

  /** Activo */
  activo: boolean;
}
