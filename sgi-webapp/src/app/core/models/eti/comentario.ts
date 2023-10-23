import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { DateTime } from 'luxon';
import { IPersona } from '../sgp/persona';
import { IApartado } from './apartado';
import { IEvaluacion } from './evaluacion';
import { IMemoria } from './memoria';
import { TipoComentario } from './tipo-comentario';

export interface IComentario {
  /** Id */
  id: number;
  /** Memoria */
  memoria: IMemoria;
  /** Apartado del formulario */
  apartado: IApartado;
  /** Evaluación */
  evaluacion: IEvaluacion;
  /** Tipo de comentario */
  tipoComentario: TipoComentario;
  /** Texto */
  texto: string;
  /** Persona creación/modificación */
  evaluador: IPersona;
  /** Estado */
  estado: TipoEstadoComentario
  /** Fecha Estado */
  fechaEstado: DateTime;
}

export enum TipoEstadoComentario {
  /** ABIERTO */
  ABIERTO = 'ABIERTO',
  /** CERRADO */
  CERRADO = 'CERRADO'
}

export const TIPO_ESTADO_COMENTARIO_MAP: Map<TipoEstadoComentario, string> = new Map([
  [TipoEstadoComentario.ABIERTO, marker(`eti.comentario.estado.ABIERTO`)],
  [TipoEstadoComentario.CERRADO, marker(`eti.comentario.estado.CERRADO`)]
]);
