import { DateTime } from 'luxon';
import { IAutorizacion } from './autorizacion';

export interface IAutorizacionWithFirstEstado extends IAutorizacion {
  fechaFirstEstado: DateTime;
}
