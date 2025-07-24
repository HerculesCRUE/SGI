import { IModeloEjecucionResponse } from '@core/services/csp/modelo-ejecucion/modelo-ejecucion-response';
import { IModeloTipoFaseResponse } from "../modelo-tipo-fase/modelo-tipo-fase-response";
import { ITipoDocumentoResponse } from "../tipo-documento/tipo-documento-response";

export interface IModeloTipoDocumentoResponse {
  id: number;
  tipoDocumento: ITipoDocumentoResponse;
  modeloEjecucion: IModeloEjecucionResponse;
  modeloTipoFase: IModeloTipoFaseResponse;
  activo: boolean;
}
