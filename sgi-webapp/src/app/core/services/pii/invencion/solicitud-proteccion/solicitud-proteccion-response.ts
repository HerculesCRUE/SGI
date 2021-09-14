import { IInvencion } from '@core/models/pii/invencion';
import { Estado } from '@core/models/pii/solicitud-proteccion';
import { ITipoCaducidad } from '@core/models/pii/tipo-caducidad';
import { IViaProteccion } from '@core/models/pii/via-proteccion';

export interface ISolicitudProteccionResponse {

  id: number;
  invencion: IInvencion;
  titulo: string;
  fechaPrioridadSolicitud: string;
  fechaFinPriorPresFasNacRec: string;
  fechaPublicacion: string;
  fechaConcesion: string;
  fechaCaducidad: string;
  viaProteccion: IViaProteccion;
  numeroSolicitud: string;
  numeroPublicacion: string;
  numeroConcesion: string;
  numeroRegistro: string;
  estado: Estado;
  tipoCaducidad: ITipoCaducidad;
  agentePropiedadRef: string;
  paisProteccionRef: string;
  comentarios: string;
  activo: boolean;

}
