import { DateTime } from 'luxon';

export interface IConvocatoriaBaremacion {
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
  fechaInicioEjecucion: DateTime;
  fechaFinEjecucion: DateTime;
  activo: boolean;
}
