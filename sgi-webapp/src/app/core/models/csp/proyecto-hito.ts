import { DateTime } from 'luxon';
import { IGenericEmailText } from '../com/generic-email-text';
import { ISendEmailTask } from '../tp/send-email-task';
import { ITipoHito } from './tipos-configuracion';

export interface IProyectoHito {
  /** Id */
  id: number;
  /** Id de Proyecto */
  proyectoId: number;
  /** Tipo de hito */
  tipoHito: ITipoHito;
  /** Fecha  */
  fecha: DateTime;
  /** Comentario */
  comentario: string;

  aviso: {
    email: IGenericEmailText;
    task: ISendEmailTask;
    incluirIpsProyecto: boolean;
  };
}
