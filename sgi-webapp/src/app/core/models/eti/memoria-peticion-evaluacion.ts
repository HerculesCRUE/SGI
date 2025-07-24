import { I18nFieldValue } from '@core/i18n/i18n-field';
import { DateTime } from 'luxon';
import { IPersona } from '../sgp/persona';
import { IComite } from './comite';
import { IRetrospectiva } from './retrospectiva';
import { TipoEstadoMemoria } from './tipo-estado-memoria';

export interface IMemoriaPeticionEvaluacion {
  /** Id */
  id: number;
  /** ResponsableRef */
  responsableRef: string;
  /** Numero Referencia */
  numReferencia: string;
  /** Título */
  titulo: I18nFieldValue[];
  /** Comité */
  comite: IComite;
  /** Estado Memoria Actual */
  estadoActual: TipoEstadoMemoria;
  /** Indicador require retrospectiva */
  requiereRetrospectiva: boolean;
  /** Retrospectiva */
  retrospectiva: IRetrospectiva;
  /** Fecha evaluación. */
  fechaEvaluacion: DateTime;
  /** 	Fecha límite. */
  fechaLimite: DateTime;
  /** Responsable de memoria */
  isResponsable: boolean;
  /** activo */
  activo: boolean;
  /** Solicitante peticion evaluacion */
  solicitante: IPersona;
  /** versión */
  version: number;
}
