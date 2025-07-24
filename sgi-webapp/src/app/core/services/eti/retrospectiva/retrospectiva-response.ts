import { IEstadoRetrospectiva } from '../../../models/eti/estado-retrospectiva';

export interface IRetrospectivaResponse {
  /** ID */
  id: number;
  /** estadoRetrospectiva */
  estadoRetrospectiva: IEstadoRetrospectiva;
  /** fechaRetrospectiva */
  fechaRetrospectiva: string;
}
