import { IActa } from './acta';
import { IInformeDocumento } from './informe-documento';
import { IMemoria } from './memoria';
import { TipoEvaluacion } from './tipo-evaluacion';

export interface IInforme {
  /** Id */
  id: number;
  /** Memoria */
  memoria: IMemoria;
  /** referencia documentos */
  documentos: IInformeDocumento[];
  /** Version */
  version: number;
  /** Tipo Evaluación */
  tipoEvaluacion: TipoEvaluacion;
}
