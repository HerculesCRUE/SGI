import { IModeloEjecucionResponse } from "../modelo-ejecucion/modelo-ejecucion-response";
import { ITipoEnlaceResponse } from "../tipo-enlace/tipo-enlace-response";

export interface IModeloTipoEnlaceResponse {
  id: number;
  tipoEnlace: ITipoEnlaceResponse;
  modeloEjecucion: IModeloEjecucionResponse;
  activo: boolean;
}
