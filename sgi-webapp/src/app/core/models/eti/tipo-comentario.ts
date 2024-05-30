export enum TIPO_COMENTARIO {
  GESTOR = 1,
  EVALUADOR = 2,
  ACTA_GESTOR = 3,
  ACTA_EVALUADOR = 4
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
