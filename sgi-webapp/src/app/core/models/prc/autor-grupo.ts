import { IAutor } from './autor';
import { TipoEstadoProduccion } from './estado-produccion-cientifica';

export interface IAutorGrupo {
  id: number;
  estado: TipoEstadoProduccion;
  // TODO cambiar a IGrupo cuando existe la entidad
  grupoRef: string;
  autor: IAutor;
}
