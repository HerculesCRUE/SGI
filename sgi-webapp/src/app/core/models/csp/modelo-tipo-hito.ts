import { IModeloEjecucion, ITipoHito } from './tipos-configuracion';

export interface IModeloTipoHito {
  id: number;
  tipoHito: ITipoHito;
  modeloEjecucion: IModeloEjecucion;
  convocatoria: boolean;
  proyecto: boolean;
  solicitud: boolean;
  activo: boolean;
}
