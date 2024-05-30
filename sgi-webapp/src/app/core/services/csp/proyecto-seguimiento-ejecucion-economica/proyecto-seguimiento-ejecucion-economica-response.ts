export interface IProyectoSeguimientoEjecucionEconomicaResponse {
  id: number;
  proyectoId: number;
  proyectoSgeRef: string;
  nombre: string;
  codigoExterno: string;
  fechaInicio: string;
  fechaFin: string;
  fechaFinDefinitiva: string;
  tituloConvocatoria: string;
  importeConcedido: number;
  importeConcedidoCostesIndirectos: number;
}
