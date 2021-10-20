export enum TIPO_COMENTARIO {
  GESTOR = 1,
  EVALUADOR = 2,
  ACTA = 3
}

export class TipoComentario {
  /** Id */
  id: number;

  /** Nombre */
  nombre: string;

  /** Activo */
  activo: boolean;

  constructor(tipoComentario?: TipoComentario) {
    this.id = tipoComentario?.id;
    this.nombre = tipoComentario?.nombre;
    this.activo = tipoComentario?.activo;
  }
}
