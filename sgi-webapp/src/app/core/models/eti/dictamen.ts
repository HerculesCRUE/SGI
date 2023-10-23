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
