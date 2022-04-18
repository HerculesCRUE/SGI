export interface IConvocatoriaBaremacionResponse {
  id: number;
  nombre: string;
  anio: number;
  aniosBaremables: number;
  ultimoAnio: number;
  importeTotal: number;
  partidaPresupuestaria: string;
  puntoProduccion: number;
  puntoSexenio: number;
  puntoCostesIndirectos: number;
  fechaInicioEjecucion: string;
  fechaFinEjecucion: string;
  activo: boolean;
}
