import { I18nFieldValueResponse } from '@core/i18n/i18n-field-response';
import { TipoEntidad } from '@core/models/rel/relacion';

export interface IRelacionResponse {
  id: number;
  tipoEntidadOrigen: TipoEntidad;
  tipoEntidadDestino: TipoEntidad;
  entidadOrigenRef: string;
  entidadDestinoRef: string;
  observaciones: I18nFieldValueResponse[];
}