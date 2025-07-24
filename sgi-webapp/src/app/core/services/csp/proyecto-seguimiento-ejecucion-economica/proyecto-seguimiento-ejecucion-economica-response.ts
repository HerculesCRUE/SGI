import { I18nFieldValueResponse } from "@core/i18n/i18n-field-response";

export interface IProyectoSeguimientoEjecucionEconomicaResponse {
  id: number;
  proyectoId: number;
  proyectoSgeRef: string;
  nombre: I18nFieldValueResponse[];
  codigoExterno: string;
  fechaInicio: string;
  fechaFin: string;
  fechaFinDefinitiva: string;
  tituloConvocatoria: I18nFieldValueResponse[];
  importeConcedido: number;
  importeConcedidoCostesIndirectos: number;
}
