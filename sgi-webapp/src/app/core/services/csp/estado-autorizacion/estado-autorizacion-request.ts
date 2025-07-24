import { I18nFieldValueRequest } from '@core/i18n/i18n-field-request';
import { Estado } from '@core/models/csp/estado-autorizacion';

export interface IEstadoAutorizacionRequest {
  autorizacionId: number;
  comentario: I18nFieldValueRequest[];
  fecha: string;
  estado: Estado;
}
