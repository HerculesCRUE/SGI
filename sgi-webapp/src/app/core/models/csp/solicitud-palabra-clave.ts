import { ISolicitud } from './solicitud';

export interface ISolicitudPalabraClave {
  id: number;
  solicitud: ISolicitud;
  palabraClave: string;
}
