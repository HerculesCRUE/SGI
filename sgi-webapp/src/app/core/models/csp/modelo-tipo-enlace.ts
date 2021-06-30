import { ITipoEnlace, IModeloEjecucion } from './tipos-configuracion';

export interface IModeloTipoEnlace {
  id: number;
  tipoEnlace: ITipoEnlace;
  modeloEjecucion: IModeloEjecucion;
  activo: boolean;
}
