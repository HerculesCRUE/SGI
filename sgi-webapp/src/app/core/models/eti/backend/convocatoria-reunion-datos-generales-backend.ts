import { IConvocatoriaReunionBackend } from './convocatoria-reunion-backend';

export interface IConvocatoriaReunionDatosGeneralesBackend extends IConvocatoriaReunionBackend {
  /** Num Evaluaciones */
  numEvaluaciones: number;
  /** id Acta */
  idActa: number;
}
