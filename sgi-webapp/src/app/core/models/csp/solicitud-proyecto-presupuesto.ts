import { IPartidaGasto } from './partida-gasto';
import { ISolicitudProyectoEntidad } from './solicitud-proyecto-entidad';

export interface ISolicitudProyectoPresupuesto extends IPartidaGasto {
  solicitudProyectoId: number;
  solicitudProyectoEntidad: ISolicitudProyectoEntidad;
  importePresupuestado: number;
}
