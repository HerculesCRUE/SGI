import { ISectorAplicacionResponse } from "../../sector-aplicacion/sector-aplicacion-response";

export interface IInvencionSectorAplicacionResponse {
  id: number;
  invencionId: number;
  sectorAplicacion: ISectorAplicacionResponse;
}
