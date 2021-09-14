import { INivelAcademico } from '../sgp/nivel-academico';
import { IConvocatoriaRequisitoIP } from './convocatoria-requisito-ip';

export interface IRequisitoIPNivelAcademico {
  id: number;
  requisitoIP: IConvocatoriaRequisitoIP;
  nivelAcademico: INivelAcademico;
}
