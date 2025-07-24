import { IActaDocumentoResponse } from '@core/services/eti/acta/acta-documento-response';
import { TipoConvocatoriaReunion } from '../../../models/eti/tipo-convocatoria-reunion';
import { TipoEstadoActa } from '../../../models/eti/tipo-estado-acta';

export interface IActaWithNumEvaluacionesResponse {
  /** ID */
  id: number;
  /** Comite */
  comite: string;
  /** Fecha evaluación */
  fechaEvaluacion: string;
  /** Numero acta */
  numeroActa: number;
  /** Tipo Convocatoria */
  tipoConvocatoria: TipoConvocatoriaReunion;
  /** Nº de evaluaciones (iniciales) */
  numEvaluaciones: number;
  /** Nº de revisiones */
  numRevisiones: number;
  /** Nº total */
  numTotal: number;
  /** Estado del acta */
  estadoActa: TipoEstadoActa;
  /** Número de evaluacines no evaluadas. */
  numEvaluacionesNoEvaluadas: number;
  /** referencia documentos */
  documentos: IActaDocumentoResponse[];
}
