import { TipoEvaluacion } from '../../../models/eti/tipo-evaluacion';
import { IInformeDocumentoResponse } from './informe-documento-response';
import { IMemoriaResponse } from './memoria-response';

export interface IInformeResponse {
  /** Id */
  id: number;
  /** Memoria */
  memoria: IMemoriaResponse;
  /** referencia */
  informeDocumentos: IInformeDocumentoResponse[];
  /** Version */
  version: number;
  /** Tipo Evaluación */
  tipoEvaluacion: TipoEvaluacion;
}
