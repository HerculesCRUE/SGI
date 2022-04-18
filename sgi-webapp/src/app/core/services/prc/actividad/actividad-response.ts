import { TipoEstadoProduccion } from '@core/models/prc/estado-produccion-cientifica';

export interface IActividadResponse {
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
  tituloActividad: string;
}
