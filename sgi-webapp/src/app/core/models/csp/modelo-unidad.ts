import { IModeloEjecucion } from './tipos-configuracion';
import { IUnidadGestion } from '@core/models/usr/unidad-gestion';

export interface IModeloUnidad {
  id: number;
  unidadGestion: IUnidadGestion;
  modeloEjecucion: IModeloEjecucion;
  activo: boolean;
}
