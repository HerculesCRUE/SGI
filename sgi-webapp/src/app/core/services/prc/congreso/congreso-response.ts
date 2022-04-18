import { TipoEvento } from '@core/models/prc/congreso';
import { TipoEstadoProduccion } from '@core/models/prc/estado-produccion-cientifica';

export interface ICongresoResponse {
  id: number;
  produccionCientificaRef: string;
  epigrafe: string;
  estado: {
    id: number;
    estado: TipoEstadoProduccion;
    fecha: string;
    comentario: string;
  };
  fechaCelebracion: string;
  tipoEvento: TipoEvento;
  tituloTrabajo: string;
}
