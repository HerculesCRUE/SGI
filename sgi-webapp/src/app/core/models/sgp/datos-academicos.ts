import { DateTime } from 'luxon';
import { INivelAcademico } from './nivel-academico';
export interface IDatosAcademicos {
  id: string;
  nivelAcademico: INivelAcademico;
  fechaObtencion: DateTime;
}
