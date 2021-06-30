import { IModeloEjecucion } from '../tipos-configuracion';

export interface IModeloUnidadBackend {
  id: number;
  unidadGestionRef: string;
  modeloEjecucion: IModeloEjecucion;
  activo: boolean;
}
