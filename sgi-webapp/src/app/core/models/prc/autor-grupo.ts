import { IGrupo } from '../csp/grupo';
import { IAutor } from './autor';
import { TipoEstadoProduccion } from './estado-produccion-cientifica';

export interface IAutorGrupo {
  id: number;
  estado: TipoEstadoProduccion;
  grupo: IGrupo;
  autor: IAutor;
}
