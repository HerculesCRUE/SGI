import { IComite } from './comite';
import { TIPO_EVALUACION } from './tipo-evaluacion';

export enum FORMULARIO {
  M10 = 1,
  M20 = 2,
  M30 = 3,
  SEGUIMIENTO_ANUAL = 4,
  SEGUIMIENTO_FINAL = 5,
  RETROSPECTIVA = 6
}

export function resolveFormularioByTipoEvaluacionAndComite(tipoEvaluacion: TIPO_EVALUACION, comite: IComite): FORMULARIO {
  switch (tipoEvaluacion) {
    case TIPO_EVALUACION.MEMORIA:
      return comite.formulario.id as FORMULARIO;
    case TIPO_EVALUACION.RETROSPECTIVA:
      return FORMULARIO.RETROSPECTIVA;
    case TIPO_EVALUACION.SEGUIMIENTO_ANUAL:
      return FORMULARIO.SEGUIMIENTO_ANUAL;
    case TIPO_EVALUACION.SEGUIMIENTO_FINAL:
      return FORMULARIO.SEGUIMIENTO_FINAL;
    default:
      return null;
  }
}

export interface IFormulario {
  /** Id */
  id: number;

  /** Nombre */
  nombre: string;

  /** Descripción */
  descripcion: string;

  /** Activo */
  activo: boolean;
}
