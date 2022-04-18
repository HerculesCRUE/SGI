import { TipoEstadoProduccion } from '@core/models/prc/estado-produccion-cientifica';

export interface IObraArtisticaResponse {
  id: number;
  produccionCientificaRef: string;
  epigrafe: string;
  estado: {
    id: number;
    estado: TipoEstadoProduccion;
    fecha: string;
    comentario: string;
  };
  fechaInicio: string;
  descripcion: string;
}
