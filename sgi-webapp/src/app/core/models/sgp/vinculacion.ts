import { DateTime } from 'luxon';
import { ICategoriaProfesional } from './categoria-profesional';

export interface IVinculacion {
  id: string;
  categoriaProfesional: ICategoriaProfesional;
  fechaObtencionCategoria: DateTime;
}
