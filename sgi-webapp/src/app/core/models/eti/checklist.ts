import { DateTime } from 'luxon';
import { IPersona } from '../sgp/persona';
import { IFormly } from './formly';

export interface IChecklist {
  id: number;
  persona: IPersona;
  formly: IFormly;
  respuesta: {
    [name: string]: any;
  };
  fechaCreacion: DateTime;
}
