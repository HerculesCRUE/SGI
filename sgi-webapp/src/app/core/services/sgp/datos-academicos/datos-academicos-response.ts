import { DateTime } from 'luxon';
import { INivelAcademico } from '../../../models/sgp/nivel-academico';
export interface IDatosAcademicosResponse {
  id: string;
  nivelAcademico: INivelAcademico;
  fechaObtencion: string;
}
