import { IAreaConocimiento } from '../sgo/area-conocimiento';

export interface ISolicitudProyectoAreaConocimiento {
  /** Id */
  id: number;
  /** Solicitud Proyecto Id */
  solicitudProyectoId: number;
  /** Area de Conocimiento  */
  areaConocimiento: IAreaConocimiento;
}
