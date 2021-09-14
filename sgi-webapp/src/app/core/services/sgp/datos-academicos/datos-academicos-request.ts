import { DateTime } from 'luxon';
import { INivelAcademico } from '../../../models/sgp/nivel-academico';
export interface IDatosAcademicosRequest {
  id: string;
  nivelAcademico: INivelAcademico;
  fechaObtencion: string;
}
