import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { TipoEvaluacion } from './tipo-evaluacion';

export enum DICTAMEN {
  FAVORABLE = 1,
  FAVORABLE_PDTE_REV_MINIMA = 2,
  PDTE_CORRECCIONES = 3,
  NO_PROCEDE_EVALUAR = 4,
  FAVORABLE_SEG_ANUAL = 5,
  SOLICITUD_MODIFICACIONES_SEG_ANUAL = 6,
  FAVORABLE_SEG_FINAL = 7,
  SOLICITUD_ACLARACIONES_SEG_FINAL = 8,
  FAVORABLE_RETROSPECTIVA = 9,
  DESFAVORABLE_RETROSPECTIVA = 10,
  DESFAVORABLE = 11
}

export const DICTAMEN_MAP: Map<DICTAMEN, string> = new Map([
  [DICTAMEN.FAVORABLE, marker(`eti.dictamen.FAVORABLE`)],
  [DICTAMEN.FAVORABLE_PDTE_REV_MINIMA, marker(`eti.dictamen.FAVORABLE_PDTE_REV_MINIMA`)],
  [DICTAMEN.PDTE_CORRECCIONES, marker(`eti.dictamen.PDTE_CORRECCIONES`)],
  [DICTAMEN.NO_PROCEDE_EVALUAR, marker(`eti.dictamen.NO_PROCEDE_EVALUAR`)],
  [DICTAMEN.FAVORABLE_SEG_ANUAL, marker(`eti.dictamen.FAVORABLE_SEG_ANUAL`)],
  [DICTAMEN.SOLICITUD_MODIFICACIONES_SEG_ANUAL, marker(`eti.dictamen.SOLICITUD_MODIFICACIONES_SEG_ANUAL`)],
  [DICTAMEN.FAVORABLE_SEG_FINAL, marker(`eti.dictamen.FAVORABLE_SEG_FINAL`)],
  [DICTAMEN.SOLICITUD_ACLARACIONES_SEG_FINAL, marker(`eti.dictamen.SOLICITUD_ACLARACIONES_SEG_FINAL`)],
  [DICTAMEN.FAVORABLE_RETROSPECTIVA, marker(`eti.dictamen.FAVORABLE_RETROSPECTIVA`)],
  [DICTAMEN.DESFAVORABLE_RETROSPECTIVA, marker(`eti.dictamen.DESFAVORABLE_RETROSPECTIVA`)],
  [DICTAMEN.DESFAVORABLE, marker(`eti.dictamen.DESFAVORABLE`)]
]);

export interface IDictamen {
  /** Id */
  id: number;

  /** Nombre */
  nombre: string;

  /** Activo */
  activo: boolean;

  /** Tipo evaluacion */
  tipoEvaluacion: TipoEvaluacion;

}
