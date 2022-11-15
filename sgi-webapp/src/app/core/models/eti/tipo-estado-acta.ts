import { marker } from "@biesbjerg/ngx-translate-extract-marker";

export enum ESTADO_ACTA {
  EN_ELABORACION = 1,
  FINALIZADA = 2,
}

export class TipoEstadoActa {

  /** ID */
  id: number;
  /** Nombre */
  nombre: string;
  /** Activo */
  activo: boolean;

  constructor() {
    this.id = null;
    this.nombre = null;
    this.activo = true;
  }

}

export const ESTADO_ACTA_MAP: Map<ESTADO_ACTA, string> = new Map([
  [ESTADO_ACTA.EN_ELABORACION, marker(`eti.estado-acta.EN_ELABORACION`)],
  [ESTADO_ACTA.FINALIZADA, marker(`eti.estado-acta.FINALIZADA`)]
]);
