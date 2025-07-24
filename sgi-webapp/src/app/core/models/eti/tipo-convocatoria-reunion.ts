import { marker } from '@biesbjerg/ngx-translate-extract-marker';

export enum TIPO_CONVOCATORIA_REUNION {
  ORDINARIA = 1,
  EXTRAORDINARIA = 2,
  SEGUIMIENTO = 3
}

export const TIPO_CONVOCATORIA_REUNION_MAP: Map<TIPO_CONVOCATORIA_REUNION, string> = new Map([
  [TIPO_CONVOCATORIA_REUNION.EXTRAORDINARIA, marker(`eti.tipo-convocatoria-reunion.EXTRAORDINARIA`)],
  [TIPO_CONVOCATORIA_REUNION.ORDINARIA, marker(`eti.tipo-convocatoria-reunion.ORDINARIA`)],
  [TIPO_CONVOCATORIA_REUNION.SEGUIMIENTO, marker(`eti.tipo-convocatoria-reunion.SEGUIMIENTO`)]
]);

export class TipoConvocatoriaReunion {

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
