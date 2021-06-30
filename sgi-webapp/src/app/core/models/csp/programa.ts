export interface IPrograma {
  /** Id */
  id: number;

  /** Nombre  */
  nombre: string;

  /** descripcion  */
  descripcion: string;

  /** padre  */
  padre: IPrograma;

  /** activo  */
  activo: boolean;
}
