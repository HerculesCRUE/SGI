import { ICategoriaProfesional } from '../sgp/categoria-profesional';
import { IConvocatoriaRequisitoEquipo } from './convocatoria-requisito-equipo';

export interface IRequisitoEquipoCategoriaProfesional {
  id: number;
  categoriaProfesional: ICategoriaProfesional;
  requisitoEquipo: IConvocatoriaRequisitoEquipo;
}
