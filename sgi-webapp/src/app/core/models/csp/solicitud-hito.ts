import { DateTime } from 'luxon';
import { IGenericEmailText } from '../com/generic-email-text';
import { ISendEmailTask } from '../tp/send-email-task';
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
  /** Tipo Hito */
  tipoHito: ITipoHito;
  aviso: {
    email: IGenericEmailText;
    task: ISendEmailTask;
    incluirIpsSolicitud: boolean;
  };
}
