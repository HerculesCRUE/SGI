import { IComite } from '../comite';
import { TipoEstadoMemoria } from '../tipo-estado-memoria';
import { IRetrospectivaBackend } from './retrospectiva-backend';

export interface IMemoriaPeticionEvaluacionBackend {
  /** Id */
  id: number;
  /** Respnsable Ref */
  responsableRef: string;
  /** Numero Referencia  */
  numReferencia: string;
  /** Título */
  titulo: string;
  /** Comité */
  comite: IComite;
  /** Estado Memoria Actual */
  estadoActual: TipoEstadoMemoria;
  /** Indicador require retrospectiva */
  requiereRetrospectiva: boolean;
  /** Retrospectiva */
  retrospectiva: IRetrospectivaBackend;
  /** Fecha evaluación. */
  fechaEvaluacion: string;
  /** 	Fecha límite. */
  fechaLimite: string;
  /** Responsable de memoria */
  isResponsable: boolean;
  /** activo */
  activo: boolean;
  /** Solicitante peticion evaluacion */
  solicitanteRef;
}
