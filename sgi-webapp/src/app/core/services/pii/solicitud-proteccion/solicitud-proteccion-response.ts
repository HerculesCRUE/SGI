import { I18nFieldValueResponse } from '@core/i18n/i18n-field-response';
import { IInvencion } from '@core/models/pii/invencion';
import { Estado } from '@core/models/pii/solicitud-proteccion';
import { ITipoCaducidad } from '@core/models/pii/tipo-caducidad';
import { IViaProteccionResponse } from '../via-proteccion/via-proteccion-response';
import { IInvencionResponse } from '../invencion/invencion-response';
import { ITipoCaducidadResponse } from '../tipo-caducidad/tipo-caducidad-response';

export interface ISolicitudProteccionResponse {
  id: number;
  invencion: IInvencionResponse;
  titulo: I18nFieldValueResponse[];
  fechaPrioridadSolicitud: string;
  fechaFinPriorPresFasNacRec: string;
  fechaPublicacion: string;
  fechaConcesion: string;
  fechaCaducidad: string;
  viaProteccion: IViaProteccionResponse;
  numeroSolicitud: string;
  numeroPublicacion: string;
  numeroConcesion: string;
  numeroRegistro: string;
  estado: Estado;
  tipoCaducidad: ITipoCaducidadResponse;
  agentePropiedadRef: string;
  paisProteccionRef: string;
  comentarios: I18nFieldValueResponse[];
}
