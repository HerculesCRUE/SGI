import { I18nFieldValueResponse } from '@core/i18n/i18n-field-response';
import { MemoriaTipo } from '@core/models/eti/memoria';
import { TipoEstadoMemoria } from '../../../models/eti/tipo-estado-memoria';
import { IComiteResponse } from '../comite/comite-response';
import { IFormularioResponse } from '../formulario/formulario-response';
import { IPeticionEvaluacionResponse } from '../peticion-evaluacion/peticion-evaluacion-response';
import { IRetrospectivaResponse } from '../retrospectiva/retrospectiva-response';

export interface IMemoriaResponse {
  /** Id */
  id: number;
  /** Referencia */
  numReferencia: string;
  /** Petición evaluación */
  peticionEvaluacion: IPeticionEvaluacionResponse;
  /** Comité */
  comite: IComiteResponse;
  /** Formulario de la memoria */
  formulario: IFormularioResponse;
  /** Formulario del Seguimiento Anual */
  formularioSeguimientoAnual: IFormularioResponse;
  /** Formulario del Seguimiento Final */
  formularioSeguimientoFinal: IFormularioResponse;
  /** Formulario de la Retrospectiva */
  formularioRetrospectiva: IFormularioResponse;
  /** Título */
  titulo: I18nFieldValueResponse[];
  //** Referencia persona responsable */
  personaRef: string;
  /** Tipo Memoria */
  tipo: MemoriaTipo;
  /** Fecha envio secretaria. */
  fechaEnvioSecretaria: string;
  /** Indicador require retrospectiva */
  requiereRetrospectiva: boolean;
  /** Version */
  version: number;
  /** Estado Memoria Actual */
  estadoActual: TipoEstadoMemoria;
  /** Responsable de memoria */
  isResponsable: boolean;
  /** Retrospectiva */
  retrospectiva: IRetrospectivaResponse;
  /** Memoria original */
  memoriaOriginal: IMemoriaResponse;
  /** Activo */
  activo: boolean;
}
