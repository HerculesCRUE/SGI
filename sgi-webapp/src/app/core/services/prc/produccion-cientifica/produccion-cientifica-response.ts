import { TipoEstadoProduccion } from '@core/models/prc/estado-produccion-cientifica';

export interface IProduccionCientificaResponse {
  id: number;
  epigrafe: string;
  estado: {
    id: number;
    estado: TipoEstadoProduccion;
    fecha: string;
    comentario: string;
  };
  produccionCientificaRef: string;
}
