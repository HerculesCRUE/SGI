import { TipoEntidad } from '@core/models/rel/relacion';

export interface IRelacionRequest {
  tipoEntidadOrigen: TipoEntidad;
  tipoEntidadDestino: TipoEntidad;
  entidadOrigenRef: string;
  entidadDestinoRef: string;
  observaciones: string;
}