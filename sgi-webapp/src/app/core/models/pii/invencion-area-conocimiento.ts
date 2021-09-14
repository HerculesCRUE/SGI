import { IAreaConocimiento } from "../sgo/area-conocimiento";
import { IInvencion } from "./invencion";

export interface IInvencionAreaConocimiento {
  id: number;
  invencion: IInvencion;
  areaConocimiento: IAreaConocimiento;
}