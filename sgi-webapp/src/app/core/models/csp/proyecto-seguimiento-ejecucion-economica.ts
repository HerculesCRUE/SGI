import { I18nFieldValue } from '@core/i18n/i18n-field';
import { DateTime } from 'luxon';

export interface IProyectoSeguimientoEjecucionEconomica {
  /** ProyectoProyectoSgeId */
  id: number;
  proyectoId: number;
  proyectoSgeRef: string;
  nombre: I18nFieldValue[];
  codigoExterno: string;
  fechaInicio: DateTime;
  fechaFin: DateTime;
  fechaFinDefinitiva: DateTime;
  tituloConvocatoria: I18nFieldValue[];
  importeConcedido: number;
  importeConcedidoCostesIndirectos: number;
}
