import { IMemoria } from './memoria';
import { TIPO_EVALUACION } from './tipo-evaluacion';


export function resolveFormularioByTipoEvaluacionAndComite(tipoEvaluacion: TIPO_EVALUACION, memoria: IMemoria): IFormulario {
  switch (tipoEvaluacion) {
    case TIPO_EVALUACION.MEMORIA:
      return memoria.formulario;
    case TIPO_EVALUACION.RETROSPECTIVA:
      return memoria.formularioRetrospectiva;
    case TIPO_EVALUACION.SEGUIMIENTO_ANUAL:
      return memoria.formularioSeguimientoAnual;
    case TIPO_EVALUACION.SEGUIMIENTO_FINAL:
      return memoria.formularioSeguimientoFinal;
    default:
      return null;
  }
}

export interface IFormulario {
  /** Id */
  id: number;
  /** Tipo */
  tipo: FormularioTipo;
  /** Codigo */
  codigo: string;
  /** Titulo del apartado Seguimiento Anual en la documentación de la Memoria */
  seguimientoAnualDocumentacionTitle: FormularioSeguimientoAnualDocumentacionTitle;
  /** Activo */
  activo: boolean;
}

export enum FormularioTipo {
  MEMORIA = 'MEMORIA',
  SEGUIMIENTO_ANUAL = 'SEGUIMIENTO_ANUAL',
  SEGUIMIENTO_FINAL = 'SEGUIMIENTO_FINAL',
  RETROSPECTIVA = 'RETROSPTECTIVA'
}

export enum FormularioSeguimientoAnualDocumentacionTitle {
  TITULO_1 = 'TITULO_1',
  TITULO_2 = 'TITULO_2',
  TITULO_3 = 'TITULO_3'
}