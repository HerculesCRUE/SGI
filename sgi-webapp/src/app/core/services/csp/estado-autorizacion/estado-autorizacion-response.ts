import { I18nFieldValueResponse } from '@core/i18n/i18n-field-response';
import { Estado } from '@core/models/csp/estado-autorizacion';

export interface IEstadoAutorizacionResponse {
  id: number;
  autorizacionId: number;
  comentario: I18nFieldValueResponse[];
  fecha: string;
  estado: Estado;
}
