import { DateTime } from 'luxon';
import { ISolicitudProteccion } from './solicitud-proteccion';
import { ITipoProcedimiento } from './tipo-procedimiento';

export interface IProcedimiento {

  id: number;
  fecha: DateTime;
  tipoProcedimiento: ITipoProcedimiento;
  solicitudProteccion: ISolicitudProteccion;
  accionATomar: string;
  fechaLimiteAccion: DateTime;
  generarAviso: boolean;
  comentarios: string;

}
