import { IConvocatoriaReunionResponse } from './convocatoria-reunion-response';

export interface IConvocatoriaReunionDatosGeneralesResponse extends IConvocatoriaReunionResponse {
  /** Num Evaluaciones */
  numEvaluaciones: number;
  /** id Acta */
  idActa: number;
}
