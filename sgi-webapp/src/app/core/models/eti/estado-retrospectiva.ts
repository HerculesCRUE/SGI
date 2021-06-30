export enum ESTADO_RETROSPECTIVA {
  PENDIENTE = 1,
  COMPLETADA = 2,
  EN_SECRETARIA = 3,
  EN_EVALUACION = 4,
  FIN_EVALUACION = 5
}

export interface IEstadoRetrospectiva {
  /** ID */
  id: number;
  /** nombre */
  nombre: string;
  /** activo */
  activo: boolean;
}
