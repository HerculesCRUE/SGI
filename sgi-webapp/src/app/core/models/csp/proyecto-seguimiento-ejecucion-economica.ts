import { DateTime } from 'luxon';

export interface IProyectoSeguimientoEjecucionEconomica {
  id: number;
  proyectoId: number;
  proyectoSgeRef: string;
  nombre: string;
  codigoExterno: string;
  fechaInicio: DateTime;
  fechaFin: DateTime;
  tituloConvocatoria: string;
  importeConcedido: number;
  importeConcedidoCostesIndirectos: number;
}
