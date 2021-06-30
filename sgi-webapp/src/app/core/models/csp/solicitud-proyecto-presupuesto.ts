import { IEmpresa } from '../sgemp/empresa';
import { IPartidaGasto } from './partida-gasto';

export interface ISolicitudProyectoPresupuesto extends IPartidaGasto {
  solicitudProyectoId: number;
  empresa: IEmpresa;
  financiacionAjena: boolean;
  importePresupuestado: number;
}
