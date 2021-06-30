import { IEntidadFinanciadora } from './entidad-financiadora';

export interface ISolicitudProyectoEntidadFinanciadoraAjena extends IEntidadFinanciadora {
  solicitudProyectoId: number;
}
