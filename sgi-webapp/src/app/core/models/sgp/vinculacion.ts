import { DateTime } from 'luxon';
import { ICentro } from '../sgo/centro';
import { IDepartamento } from '../sgo/departamento';
import { ICategoriaProfesional } from './categoria-profesional';

export interface IVinculacion {
  id: string;
  categoriaProfesional: ICategoriaProfesional;
  fechaObtencionCategoria: DateTime;
  departamento: IDepartamento;
  centro: ICentro;
}
