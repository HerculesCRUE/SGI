import { DateTime } from 'luxon';
import { IEmpresa } from '../sgemp/empresa';
import { IRolSocio } from './rol-socio';

export interface IProyectoSocio {
  id: number;
  proyectoId: number;
  empresa: IEmpresa;
  rolSocio: IRolSocio;
  fechaInicio: DateTime;
  fechaFin: DateTime;
  numInvestigadores: number;
  importeConcedido: number;
  importePresupuesto: number;
}
