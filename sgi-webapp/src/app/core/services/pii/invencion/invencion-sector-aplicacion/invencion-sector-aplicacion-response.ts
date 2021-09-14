import { ISectorAplicacion } from "@core/models/pii/sector-aplicacion";

export interface IInvencionSectorAplicacionResponse {
  id: number;
  invencionId: number;
  sectorAplicacion: ISectorAplicacion;
}
