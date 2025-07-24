import { marker } from '@biesbjerg/ngx-translate-extract-marker';

export enum TIPO_COMENTARIO {
  GESTOR = 1,
  EVALUADOR = 2,
  ACTA_GESTOR = 3,
  ACTA_EVALUADOR = 4
}

export const TIPO_COMENTARIO_MAP: Map<TIPO_COMENTARIO, string> = new Map([
  [TIPO_COMENTARIO.GESTOR, marker(`eti.tipo-comentario.GESTOR`)],
  [TIPO_COMENTARIO.EVALUADOR, marker(`eti.tipo-comentario.EVALUADOR`)],
  [TIPO_COMENTARIO.ACTA_GESTOR, marker(`eti.tipo-comentario.ACTA_GESTOR`)],
  [TIPO_COMENTARIO.ACTA_EVALUADOR, marker(`eti.tipo-comentario.ACTA_EVALUADOR`)]
]);

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
