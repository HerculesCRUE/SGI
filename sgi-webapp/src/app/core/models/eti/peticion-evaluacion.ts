import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { DateTime } from 'luxon';
import { IPersona } from '../sgp/persona';
import { ITipoActividad } from './tipo-actividad';
import { ITipoInvestigacionTutelada } from './tipo-investigacion-tutelada';

export interface IPeticionEvaluacion {
  /** ID */
  id: number;
  /** Solicitud convocatoria ref */
  solicitudConvocatoriaRef: string;
  /** Código */
  codigo: string;
  /** Título */
  titulo: string;
  /** Tipo de actividad */
  tipoActividad: ITipoActividad;
  /** Tipo de investigacion tutelada */
  tipoInvestigacionTutelada: ITipoInvestigacionTutelada;
  /** Existe fuente financiacion */
  existeFinanciacion: boolean;
  /** Referencia fuente financiacion */
  fuenteFinanciacion: string;
  /** Estado fuente financiacion */
  estadoFinanciacion: EstadoFinanciacion;
  /** Importe fuente financiacion */
  importeFinanciacion: boolean;
  /** Fecha Inicio. */
  fechaInicio: DateTime;
  /** Fecha Fin. */
  fechaFin: DateTime;
  /** Resumen */
  resumen: string;
  /** Valor social */
  valorSocial: TipoValorSocial;
  /** Otro valor social */
  otroValorSocial: string;
  /** Objetivos */
  objetivos: string;
  /** Diseño metodológico */
  disMetodologico: string;
  /** Tiene fondos propios */
  tieneFondosPropios: boolean;
  /** Referencia persona */
  solicitante: IPersona;
  /** Identificador checklist */
  checklistId: number;
  /** Referencia tutor */
  tutor: IPersona;
  /** Activo */
  activo: boolean;
}

export enum EstadoFinanciacion {
  SOLICITADO = 'SOLICITADO',
  CONCEDIDO = 'CONCEDIDO',
  DENEGADO = 'DENEGADO'
}

export enum TipoValorSocial {
  /** INVESTIGACION_FUNDAMENTAL */
  INVESTIGACION_FUNDAMENTAL = 'INVESTIGACION_FUNDAMENTAL',
  /** INVESTIGACION_PREVENCION */
  INVESTIGACION_PREVENCION = 'INVESTIGACION_PREVENCION',
  /** INVESTIGACION_EVALUACIÓN */
  INVESTIGACION_EVALUACION = 'INVESTIGACION_EVALUACION',
  /** INVESTIGACION_DESARROLLO */
  INVESTIGACION_DESARROLLO = 'INVESTIGACION_DESARROLLO',
  /** INVESTIGACION_PROTECCION */
  INVESTIGACION_PROTECCION = 'INVESTIGACION_PROTECCION',
  /** INVESTIGACION_BIENESTAR */
  INVESTIGACION_BIENESTAR = 'INVESTIGACION_BIENESTAR',
  /** INVESTIGACION_CONSERVACION */
  INVESTIGACION_CONSERVACION = 'INVESTIGACION_CONSERVACION',
  /** ENSEÑANZA_SUPERIOR */
  ENSENIANZA_SUPERIOR = 'ENSENIANZA_SUPERIOR',
  /** INVESTIGACION_JURIDICA */
  INVESTIGACION_JURIDICA = 'INVESTIGACION_JURIDICA',
  /** OTRA FINALIDAD */
  OTRA_FINALIDAD = 'OTRA_FINALIDAD'
}

export const ESTADO_FINANCIACION_MAP: Map<EstadoFinanciacion, string> = new Map([
  [EstadoFinanciacion.SOLICITADO, marker(`eti.peticion-evaluacion.estado-financiacion.SOLICITADO`)],
  [EstadoFinanciacion.CONCEDIDO, marker(`eti.peticion-evaluacion.estado-financiacion.CONCEDIDO`)],
  [EstadoFinanciacion.DENEGADO, marker(`eti.peticion-evaluacion.estado-financiacion.DENEGADO`)]
]);

export const TIPO_VALOR_SOCIAL_MAP: Map<TipoValorSocial, string> = new Map([
  [TipoValorSocial.INVESTIGACION_FUNDAMENTAL, marker(`eti.peticion-evaluacion.tipo-valor-social.INVESTIGACION_FUNDAMENTAL`)],
  [TipoValorSocial.INVESTIGACION_PREVENCION, marker(`eti.peticion-evaluacion.tipo-valor-social.INVESTIGACION_PREVENCION`)],
  [TipoValorSocial.INVESTIGACION_EVALUACION, marker(`eti.peticion-evaluacion.tipo-valor-social.INVESTIGACION_EVALUACION`)],
  [TipoValorSocial.INVESTIGACION_DESARROLLO, marker(`eti.peticion-evaluacion.tipo-valor-social.INVESTIGACION_DESARROLLO`)],
  [TipoValorSocial.INVESTIGACION_PROTECCION, marker(`eti.peticion-evaluacion.tipo-valor-social.INVESTIGACION_PROTECCION`)],
  [TipoValorSocial.INVESTIGACION_BIENESTAR, marker(`eti.peticion-evaluacion.tipo-valor-social.INVESTIGACION_BIENESTAR`)],
  [TipoValorSocial.INVESTIGACION_CONSERVACION, marker(`eti.peticion-evaluacion.tipo-valor-social.INVESTIGACION_CONSERVACION`)],
  [TipoValorSocial.ENSENIANZA_SUPERIOR, marker(`eti.peticion-evaluacion.tipo-valor-social.ENSENIANZA_SUPERIOR`)],
  [TipoValorSocial.INVESTIGACION_JURIDICA, marker(`eti.peticion-evaluacion.tipo-valor-social.INVESTIGACION_JURIDICA`)],
  [TipoValorSocial.OTRA_FINALIDAD, marker(`eti.peticion-evaluacion.tipo-valor-social.OTRA_FINALIDAD`)]
]);
