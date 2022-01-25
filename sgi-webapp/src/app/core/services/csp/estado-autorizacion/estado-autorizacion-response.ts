import { Estado } from '@core/models/csp/estado-autorizacion';

export interface IEstadoAutorizacionResponse {
  id: number;
  autorizacionId: number;
  comentario: string;
  fecha: string;
  estado: Estado;
}
