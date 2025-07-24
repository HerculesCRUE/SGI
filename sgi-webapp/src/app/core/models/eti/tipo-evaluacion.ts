import { marker } from '@biesbjerg/ngx-translate-extract-marker';

export enum TIPO_EVALUACION {
  RETROSPECTIVA = 1,
  MEMORIA = 2,
  SEGUIMIENTO_ANUAL = 3,
  SEGUIMIENTO_FINAL = 4
}

export const TIPO_EVALUACION_MAP: Map<TIPO_EVALUACION, string> = new Map([
  [TIPO_EVALUACION.RETROSPECTIVA, marker(`eti.tipo-evaluacion.RETROSPECTIVA`)],
  [TIPO_EVALUACION.MEMORIA, marker(`eti.tipo-evaluacion.MEMORIA`)],
  [TIPO_EVALUACION.SEGUIMIENTO_ANUAL, marker(`eti.tipo-evaluacion.SEGUIMIENTO_ANUAL`)],
  [TIPO_EVALUACION.SEGUIMIENTO_FINAL, marker(`eti.tipo-evaluacion.SEGUIMIENTO_FINAL`)]
]);

export class TipoEvaluacion {

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
