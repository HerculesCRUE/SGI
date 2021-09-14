import { IInvencion } from "./invencion";
import { ISectorAplicacion } from "./sector-aplicacion";

export interface IInvencionSectorAplicacion {
  id: number;
  invencion: IInvencion;
  sectorAplicacion: ISectorAplicacion;
}