import { IRolSocio } from '../rol-socio';

export interface IProyectoSocioBackend {
  id: number;
  proyectoId: number;
  empresaRef: string;
  rolSocio: IRolSocio;
  fechaInicio: string;
  fechaFin: string;
  numInvestigadores: number;
  importeConcedido: number;
  importePresupuesto: number;
}
