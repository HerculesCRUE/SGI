import { IMemoria } from './memoria';
import { TipoEvaluacion } from './tipo-evaluacion';

export interface IInforme {
  /** Id */
  id: number;
  /** Memoria */
  memoria: IMemoria;
  /** referencia */
  documentoRef: string;
  /** Version */
  version: number;
  /** Tipo Evaluación */
  tipoEvaluacion: TipoEvaluacion;
}
