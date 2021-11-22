import { DateTime } from 'luxon';
import { IPais } from '../sgo/pais';
import { ISolicitudProteccion } from './solicitud-proteccion';

export interface IPaisValidado {

  id: number;
  solicitudProteccion: ISolicitudProteccion;
  pais: IPais;
  codigoInvencion: string;
  fechaValidacion: DateTime;

}
