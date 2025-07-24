import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { I18nFieldValue } from '@core/i18n/i18n-field';
import { DateTime } from 'luxon';
import { IPersona } from '../sgp/persona';
import { IComite } from './comite';
import { IFormulario } from './formulario';
import { IPeticionEvaluacion } from './peticion-evaluacion';
import { IRetrospectiva } from './retrospectiva';
import { TipoEstadoMemoria } from './tipo-estado-memoria';

export interface IMemoria {
  /** Id */
  id: number;
  /** Referencia */
  numReferencia: string;
  /** Petición evaluación */
  peticionEvaluacion: IPeticionEvaluacion;
  /** Comité */
  comite: IComite;
  /** Formulario de la memoria */
  formulario: IFormulario;
  /** Formulario del Seguimiento Anual */
  formularioSeguimientoAnual: IFormulario;
  /** Formulario del Seguimiento Final */
  formularioSeguimientoFinal: IFormulario;
  /** Formulario de la Retrospectiva */
  formularioRetrospectiva: IFormulario;
  /** Título */
  titulo: I18nFieldValue[];
  /** Responsable */
  responsable: IPersona;
  /** Tipo Memoria */
  tipo: MemoriaTipo;
  /** Fecha envio secretaria. */
  fechaEnvioSecretaria: DateTime;
  /** Indicador require retrospectiva */
  requiereRetrospectiva: boolean;
  /** Version */
  version: number;
  /** Estado Memoria Actual */
  estadoActual: TipoEstadoMemoria;
  /** Responsable de memoria */
  isResponsable: boolean;
  /** Retrospectiva */
  retrospectiva: IRetrospectiva;
  /** Memoria original */
  memoriaOriginal: IMemoria;
  /** Activo */
  activo: boolean;
}

export enum MemoriaTipo {
  NUEVA = 'NUEVA',
  MODIFICACION = 'MODIFICACION',
  RATIFICACION = 'RATIFICACION'
}

export const MEMORIA_TIPO_MAP: Map<MemoriaTipo, string> = new Map([
  [MemoriaTipo.NUEVA, marker(`eti.memoria.tipo.NUEVA`)],
  [MemoriaTipo.MODIFICACION, marker(`eti.memoria.tipo.MODIFICACION`)],
  [MemoriaTipo.RATIFICACION, marker(`eti.memoria.tipo.RATIFICACION`)]
]);
