import { DateTime } from 'luxon';
import { ICategoriaProfesional } from './categoria-profesional';

export interface IVinculacionCategoriaProfesional {
  id: string;
  categoriaProfesional: ICategoriaProfesional;
  fechaObtencion: DateTime;
}
