import { IEstadoProduccionCientifica } from './estado-produccion-cientifica';

export interface IProduccionCientifica {
  id: number;
  epigrafe: string;
  estado: IEstadoProduccionCientifica;
  produccionCientificaRef: string;
}
