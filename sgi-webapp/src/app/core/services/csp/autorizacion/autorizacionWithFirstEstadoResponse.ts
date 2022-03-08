import { IAutorizacionResponse } from './autorizacion-response';

export interface IAutorizacionWithFirstEstadoResponse extends IAutorizacionResponse {
  fechaFirstEstado: string;
}
