import { ITipoDocumento } from './tipos-configuracion';

export interface IDocumentoRequeridoSolicitud {
  id: number;
  configuracionSolicitudId: number;
  tipoDocumento: ITipoDocumento;
  observaciones: string;
}
