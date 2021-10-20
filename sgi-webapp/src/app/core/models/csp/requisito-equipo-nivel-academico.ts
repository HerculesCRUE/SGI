import { IConvocatoriaRequisitoEquipo } from './convocatoria-requisito-equipo';
import { IRequisitoNivelAcademico } from './requisito-nivel-academico';

export interface IRequisitoEquipoNivelAcademico extends IRequisitoNivelAcademico {
  requisitoEquipo: IConvocatoriaRequisitoEquipo;
}
