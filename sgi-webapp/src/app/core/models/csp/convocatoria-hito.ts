import { DateTime } from 'luxon';
import { IGenericEmailText } from '../com/generic-email-text';
import { ISendEmailTask } from '../tp/send-email-task';
import { ITipoHito } from './tipos-configuracion';

export interface IConvocatoriaHito {
  /** Id */
  id: number;
  /** Fecha inicio  */
  fecha: DateTime;
  /** Tipo de hito */
  tipoHito: ITipoHito;
  /** Comentario */
  comentario: string;
  /** Id de Convocatoria */
  convocatoriaId: number;
  aviso: {
    email: IGenericEmailText;
    task: ISendEmailTask;
    incluirIpsSolicitud: boolean;
    incluirIpsProyecto: boolean;
  };
}
