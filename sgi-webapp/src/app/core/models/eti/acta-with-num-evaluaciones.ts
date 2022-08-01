import { DateTime } from 'luxon';
import { TipoEstadoActa } from './tipo-estado-acta';

export interface IActaWithNumEvaluaciones {
  /** ID */
  id: number;
  /** Comite */
  comite: string;
  /** Fecha evaluación */
  fechaEvaluacion: DateTime;
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
  /** Referencia al documento */
  documentoRef: string;
  /** Referencia a la transacción blockchain */
  transaccionRef: string;
}
