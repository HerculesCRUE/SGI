
import { IModeloTipoFase } from './modelo-tipo-fase';
import { IModeloEjecucion, ITipoDocumento } from './tipos-configuracion';

export interface IModeloTipoDocumento {
  id: number;
  tipoDocumento: ITipoDocumento;
  modeloEjecucion: IModeloEjecucion;
  modeloTipoFase: IModeloTipoFase;
  activo: boolean;
}
