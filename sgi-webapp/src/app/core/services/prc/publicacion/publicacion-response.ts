import { TipoEstadoProduccion } from '@core/models/prc/estado-produccion-cientifica';
import { TipoProduccion } from '@core/models/prc/publicacion';

export interface IPublicacionResponse {
  id: number;
  produccionCientificaRef: string;
  epigrafe: string;
  estado: {
    id: number;
    estado: TipoEstadoProduccion;
    fecha: string;
    comentario: string;
  };
  tituloPublicacion: string;
  tipoProduccion: TipoProduccion;
  fechaPublicacion: string;
}
