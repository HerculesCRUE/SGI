import { I18nFieldValueRequest } from '@core/i18n/i18n-field-request';
import { Estado } from '@core/models/pii/solicitud-proteccion';

export interface ISolicitudProteccionRequest {

  invencionId: number;
  titulo: I18nFieldValueRequest[];
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
  comentarios: I18nFieldValueRequest[];

}
