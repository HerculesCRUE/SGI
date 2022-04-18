import { DateTime } from 'luxon';
import { IProduccionCientifica } from './produccion-cientifica';

export interface IObraArtistica extends IProduccionCientifica {
  fechaInicio: DateTime;
  descripcion: string;
}
