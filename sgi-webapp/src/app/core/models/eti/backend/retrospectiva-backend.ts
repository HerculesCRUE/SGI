import { IEstadoRetrospectiva } from '../estado-retrospectiva';

export interface IRetrospectivaBackend {
  /** ID */
  id: number;
  /** estadoRetrospectiva */
  estadoRetrospectiva: IEstadoRetrospectiva;
  /** fechaRetrospectiva */
  fechaRetrospectiva: string;
}
