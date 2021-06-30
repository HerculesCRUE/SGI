import { DateTime } from 'luxon';
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
  /** Aviso */
  generaAviso: boolean;
}
