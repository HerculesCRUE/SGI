import { IConvocatoriaReunion } from './convocatoria-reunion';

export interface IConvocatoriaReunionDatosGenerales extends IConvocatoriaReunion {
  /** Num Evaluaciones */
  numEvaluaciones: number;
  /** id Acta */
  idActa: number;
}
