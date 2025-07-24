import { IModeloEjecucionResponse } from '@core/services/csp/modelo-ejecucion/modelo-ejecucion-response';
import { ITipoHitoResponse } from "../tipo-hito/tipo-hito-response";

export interface IModeloTipoHitoResponse {
  id: number;
  tipoHito: ITipoHitoResponse;
  modeloEjecucion: IModeloEjecucionResponse;
  convocatoria: boolean;
  proyecto: boolean;
  solicitud: boolean;
  activo: boolean;
}
