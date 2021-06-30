import { ITipoFinalidad, IModeloEjecucion } from './tipos-configuracion';

export interface IModeloTipoFinalidad {
  id: number;
  tipoFinalidad: ITipoFinalidad;
  modeloEjecucion: IModeloEjecucion;
  activo: boolean;
}
