import { TipoEstadoProduccion } from '@core/models/prc/estado-produccion-cientifica';

export interface IAutorGrupoResponse {
  id: number;
  estado: TipoEstadoProduccion;
  grupoRef: string;
  autorId: number;
}
