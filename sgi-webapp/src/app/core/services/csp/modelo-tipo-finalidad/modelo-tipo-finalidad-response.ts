import { IModeloEjecucionResponse } from '@core/services/csp/modelo-ejecucion/modelo-ejecucion-response';
import { ITipoFinalidadResponse } from "../tipo-finalidad/tipo-finalidad-response";

export interface IModeloTipoFinalidadResponse {
  id: number;
  tipoFinalidad: ITipoFinalidadResponse;
  modeloEjecucion: IModeloEjecucionResponse;
  activo: boolean;
}
