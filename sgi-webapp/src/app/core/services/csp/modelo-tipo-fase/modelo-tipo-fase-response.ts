import { IModeloEjecucionResponse } from '@core/services/csp/modelo-ejecucion/modelo-ejecucion-response';
import { ITipoFaseResponse } from "../tipo-fase/tipo-fase-response";

export interface IModeloTipoFaseResponse {
  id: number;
  tipoFase: ITipoFaseResponse;
  modeloEjecucion: IModeloEjecucionResponse;
  convocatoria: boolean;
  proyecto: boolean;
  solicitud: boolean;
  activo: boolean;
}
