import { TipoEstadoProduccion } from '@core/models/prc/estado-produccion-cientifica';

export interface IDireccionTesisResponse {
  id: number;
  produccionCientificaRef: string;
  epigrafe: string;
  estado: {
    id: number;
    estado: TipoEstadoProduccion;
    fecha: string;
    comentario: string;
  };
  fechaDefensa: string;
  tituloTrabajo: string;
}
