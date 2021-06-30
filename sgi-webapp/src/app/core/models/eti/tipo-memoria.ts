export enum TIPO_MEMORIA {
  NUEVA = 1,
  MODIFICACION = 2,
  RATIFICACION = 3
}

export interface ITipoMemoria {
  /** ID */
  id: number;
  /** Nombre */
  nombre: string;
  /** Activo */
  activo: boolean;
}
