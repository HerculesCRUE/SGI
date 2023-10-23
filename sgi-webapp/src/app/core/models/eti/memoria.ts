import { DateTime } from 'luxon';
import { IPersona } from '../sgp/persona';
import { IComite } from './comite';
import { IPeticionEvaluacion } from './peticion-evaluacion';
import { IRetrospectiva } from './retrospectiva';
import { TipoEstadoMemoria } from './tipo-estado-memoria';
import { ITipoMemoria } from './tipo-memoria';

export interface IMemoria {
  /** Id */
  id: number;

  numReferencia: string;
  /** Petición evaluación */
  peticionEvaluacion: IPeticionEvaluacion;
  /** Comité */
  comite: IComite;
  /** Título */
  titulo: string;
  /** Responsable */
  responsable: IPersona;
  /** Tipo Memoria */
  tipoMemoria: ITipoMemoria;
  /** Fecha envio secretaria. */
  fechaEnvioSecretaria: DateTime;
  /** Indicador require retrospectiva */
  requiereRetrospectiva: boolean;
  /** Version */
  version: number;
  /** Estado Memoria Actual */
  estadoActual: TipoEstadoMemoria;
  /** Responsable de memoria */
  isResponsable: boolean;
  /** Retrospectiva */
  retrospectiva: IRetrospectiva;
  /** Memoria original */
  memoriaOriginal: IMemoria;
  /** Activo */
  activo: boolean;
}
