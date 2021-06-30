import { TipoEstadoActa } from '../tipo-estado-acta';

export interface IActaWithNumEvaluacionesBackend {
  /** ID */
  id: number;
  /** Comite */
  comite: string;
  /** Fecha evaluación */
  fechaEvaluacion: string;
  /** Numero acta */
  numeroActa: number;
  /** Convocatoria */
  convocatoria: string;
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
}
