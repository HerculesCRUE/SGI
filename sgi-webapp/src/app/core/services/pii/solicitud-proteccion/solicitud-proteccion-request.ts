import { Estado } from '@core/models/pii/solicitud-proteccion';

export interface ISolicitudProteccionRequest {

  invencionId: number;
  titulo: string;
  fechaPrioridadSolicitud: string;
  fechaFinPriorPresFasNacRec: string;
  fechaPublicacion: string;
  fechaConcesion: string;
  fechaCaducidad: string;
  viaProteccionId: number;
  numeroSolicitud: string;
  numeroPublicacion: string;
  numeroConcesion: string;
  numeroRegistro: string;
  estado: Estado;
  tipoCaducidadId: number;
  agentePropiedadRef: string;
  paisProteccionRef: string;
  comentarios: string;

}
