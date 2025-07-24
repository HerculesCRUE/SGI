import { I18nFieldValueResponse } from '@core/i18n/i18n-field-response';
import { EstadoFinanciacion, TipoValorSocial } from '../../../models/eti/peticion-evaluacion';
import { ITipoActividad } from '../../../models/eti/tipo-actividad';
import { ITipoInvestigacionTutelada } from '../../../models/eti/tipo-investigacion-tutelada';

export interface IPeticionEvaluacionResponse {
  /** ID */
  id: number;
  /** Solicitud convocatoria ref */
  solicitudConvocatoriaRef: string;
  /** Código */
  codigo: string;
  /** Título */
  titulo: I18nFieldValueResponse[];
  /** Tipo de actividad */
  tipoActividad: ITipoActividad;
  /** Tipo de investigacion tutelada */
  tipoInvestigacionTutelada: ITipoInvestigacionTutelada;
  /** Existe fuente financiacion */
  existeFinanciacion: boolean;
  /** Referencia fuente financiacion */
  fuenteFinanciacion: I18nFieldValueResponse[];
  /** Estado fuente financiacion */
  estadoFinanciacion: EstadoFinanciacion;
  /** Importe fuente financiacion */
  importeFinanciacion: boolean;
  /** Fecha Inicio. */
  fechaInicio: string;
  /** Fecha Fin. */
  fechaFin: string;
  /** Resumen */
  resumen: I18nFieldValueResponse[];
  /** Valor social */
  valorSocial: TipoValorSocial;
  /** Otro valor social */
  otroValorSocial: I18nFieldValueResponse[];
  /** Objetivos */
  objetivos: I18nFieldValueResponse[];
  /** Diseño metodológico */
  disMetodologico: I18nFieldValueResponse[];
  /** Tiene fondos propios */
  tieneFondosPropios: boolean;
  /** Referencia persona solicitante */
  personaRef: string;
  /** Identificador checklist */
  checklistId: number;
  /** Referencia tutor */
  tutorRef: string;
  /** Activo */
  activo: boolean;
}
