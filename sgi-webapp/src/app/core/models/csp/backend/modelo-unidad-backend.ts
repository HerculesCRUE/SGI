import { IModeloEjecucionResponse } from '@core/services/csp/modelo-ejecucion/modelo-ejecucion-response';

export interface IModeloUnidadBackend {
  id: number;
  unidadGestionRef: string;
  modeloEjecucion: IModeloEjecucionResponse;
  activo: boolean;
}
