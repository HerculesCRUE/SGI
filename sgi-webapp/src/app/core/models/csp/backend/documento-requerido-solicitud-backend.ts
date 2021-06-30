import { ITipoDocumento } from '../tipos-configuracion';

export interface IDocumentoRequeridoSolicitudBackend {
  id: number;
  configuracionSolicitudId: number;
  tipoDocumento: ITipoDocumento;
  observaciones: string;
}
