import { IGrupoResumen } from '../csp/grupo-resumen';
import { IAutor } from './autor';
import { TipoEstadoProduccion } from './estado-produccion-cientifica';

export interface IAutorGrupo {
  id: number;
  estado: TipoEstadoProduccion;
  grupo: IGrupoResumen;
  autor: IAutor;
}
