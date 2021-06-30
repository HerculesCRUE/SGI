import { TipoEvaluacion } from '../tipo-evaluacion';
import { IMemoriaBackend } from './memoria-backend';

export interface IInformeBackend {
  /** Id */
  id: number;
  /** Memoria */
  memoria: IMemoriaBackend;
  /** referencia */
  documentoRef: string;
  /** Version */
  version: number;
  /** Tipo Evaluación */
  tipoEvaluacion: TipoEvaluacion;
}
