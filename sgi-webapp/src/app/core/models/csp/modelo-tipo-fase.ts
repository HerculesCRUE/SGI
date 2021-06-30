import { IModeloEjecucion, ITipoFase } from './tipos-configuracion';


export interface IModeloTipoFase {
  id: number;
  tipoFase: ITipoFase;
  modeloEjecucion: IModeloEjecucion;
  convocatoria: boolean;
  proyecto: boolean;
  solicitud: boolean;
  activo: boolean;
}
