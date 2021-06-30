import { DateTime } from 'luxon';
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
  /** Aviso */
  generaAviso: boolean;
}
