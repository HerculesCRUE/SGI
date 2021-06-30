import { DateTime } from 'luxon';
import { ITipoHito } from './tipos-configuracion';

export interface ISolicitudHito {
  /** Id */
  id: number;
  /** Id de Solicitud */
  solicitudId: number;
  /** fecha */
  fecha: DateTime;
  /** Comentario */
  comentario: string;
  /** Comentario */
  generaAviso: boolean;
  /** Tipo Hito */
  tipoHito: ITipoHito;
}
