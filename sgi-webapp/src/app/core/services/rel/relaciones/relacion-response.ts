import { TipoEntidad } from '@core/models/rel/relacion';

export interface IRelacionResponse {
  id: number;
  tipoEntidadOrigen: TipoEntidad;
  tipoEntidadDestino: TipoEntidad;
  entidadOrigenRef: string;
  entidadDestinoRef: string;
  observaciones: string;
}