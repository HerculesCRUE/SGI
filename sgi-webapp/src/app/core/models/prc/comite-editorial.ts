import { DateTime } from 'luxon';
import { IProduccionCientifica } from './produccion-cientifica';

export interface IComiteEditorial extends IProduccionCientifica {
  fechaInicio: DateTime;
  nombre: string;
}
