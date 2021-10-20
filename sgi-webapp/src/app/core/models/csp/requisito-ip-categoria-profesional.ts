import { ICategoriaProfesional } from '../sgp/categoria-profesional';
import { IConvocatoriaRequisitoIP } from './convocatoria-requisito-ip';

export interface IRequisitoIPCategoriaProfesional {
  id: number;
  categoriaProfesional: ICategoriaProfesional;
  requisitoIP: IConvocatoriaRequisitoIP;
}
