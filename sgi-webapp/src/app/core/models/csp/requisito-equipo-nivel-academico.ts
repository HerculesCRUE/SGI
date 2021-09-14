import { INivelAcademico } from '../sgp/nivel-academico';
import { IConvocatoriaRequisitoEquipo } from './convocatoria-requisito-equipo';

export interface IRequisitoEquipoNivelAcademico {
  id: number;
  requisitoEquipo: IConvocatoriaRequisitoEquipo;
  nivelAcademico: INivelAcademico;
}
