import { ITipoHito } from '../tipos-configuracion';

export interface ISolicitudHitoBackend {
  /** Id */
  id: number;
  /** Id de Solicitud */
  solicitudId: number;
  /** fecha */
  fecha: string;
  /** Comentario */
  comentario: string;
  /** Comentario */
  generaAviso: boolean;
  /** Tipo Hito */
  tipoHito: ITipoHito;
}
