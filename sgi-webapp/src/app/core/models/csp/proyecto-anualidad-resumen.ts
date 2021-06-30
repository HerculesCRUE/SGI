import { DateTime } from 'luxon';

export interface IProyectoAnualidadResumen {
  id: number;
  anio: number;
  fechaInicio: DateTime;
  fechaFin: DateTime;
  totalGastosPresupuesto: number;
  totalGastosConcedido: number;
  totalIngresos: number;
  presupuestar: boolean;
  enviadoSge: boolean;
}
