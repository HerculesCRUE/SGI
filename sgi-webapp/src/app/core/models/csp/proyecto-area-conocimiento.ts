import { IAreaConocimiento } from '../sgo/area-conocimiento';

export interface IProyectoAreaConocimiento {
  /** Id */
  id: number;
  /** Proyecto Id */
  proyectoId: number;
  /** Area de Conocimiento  */
  areaConocimiento: IAreaConocimiento;
}
