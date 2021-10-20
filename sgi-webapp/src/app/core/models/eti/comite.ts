import { IFormulario } from './formulario';

export enum COMITE {
  CEI = 1,
  CEEA = 2,
  CBE = 3
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
