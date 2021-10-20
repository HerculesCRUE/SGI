import { IConvocatoriaRequisitoIP } from './convocatoria-requisito-ip';
import { IRequisitoNivelAcademico } from './requisito-nivel-academico';

export interface IRequisitoIPNivelAcademico extends IRequisitoNivelAcademico {
  requisitoIP: IConvocatoriaRequisitoIP;
}
