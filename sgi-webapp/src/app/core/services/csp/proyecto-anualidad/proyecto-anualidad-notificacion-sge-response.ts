import { IEstadoProyecto } from '@core/models/csp/estado-proyecto';

export interface IProyectoAnualidadNotificacionSgeResponse {
  id: number;
  anio: number;
  proyectoFechaInicio: string;
  proyectoFechaFin: string;
  totalGastos: number;
  totalIngresos: number;
  proyectoId: number;
  proyectoTitulo: string;
  proyectoAcronimo: string;
  proyectoEstado: IEstadoProyecto;
  proyectoSgeRef: string;
  enviadoSge: boolean;
}
