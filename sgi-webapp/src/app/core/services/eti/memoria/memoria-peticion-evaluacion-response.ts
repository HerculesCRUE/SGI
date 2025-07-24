import { I18nFieldValueResponse } from '@core/i18n/i18n-field-response';
import { IComiteResponse } from '@core/services/eti/comite/comite-response';
import { TipoEstadoMemoria } from '../../../models/eti/tipo-estado-memoria';
import { IRetrospectivaResponse } from '../retrospectiva/retrospectiva-response';

export interface IMemoriaPeticionEvaluacionResponse {
  /** Id */
  id: number;
  /** Respnsable Ref */
  responsableRef: string;
  /** Numero Referencia  */
  numReferencia: string;
  /** Título */
  titulo: I18nFieldValueResponse[];
  /** Comité */
  comite: IComiteResponse;
  /** Estado Memoria Actual */
  estadoActual: TipoEstadoMemoria;
  /** Indicador require retrospectiva */
  requiereRetrospectiva: boolean;
  /** Retrospectiva */
  retrospectiva: IRetrospectivaResponse;
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
  /** versión */
  version: number;
}
