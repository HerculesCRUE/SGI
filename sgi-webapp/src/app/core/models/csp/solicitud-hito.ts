import { I18nFieldValue } from '@core/i18n/i18n-field';
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
  comentario: I18nFieldValue[];
  /** Tipo Hito */
  tipoHito: ITipoHito;
  createdBy: string;
  aviso: {
    email: IGenericEmailText;
    task: ISendEmailTask;
    incluirIpsSolicitud: boolean;
  };
}
