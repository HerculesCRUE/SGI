import { IRolSocioResponse } from '../rol-socio/rol-socio-response';

export interface IProyectoSocioResponse {
  id: number;
  proyectoId: number;
  empresaRef: string;
  rolSocio: IRolSocioResponse;
  fechaInicio: string;
  fechaFin: string;
  numInvestigadores: number;
  importeConcedido: number;
  importePresupuesto: number;
}
