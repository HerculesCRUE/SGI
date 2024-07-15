import { DateTime } from 'luxon';
import { ICategoriaProfesional } from './categoria-profesional';

export interface IVinculacionCategoriaProfesional {
  categoriaProfesional: ICategoriaProfesional;
  fechaObtencion: DateTime;
}
