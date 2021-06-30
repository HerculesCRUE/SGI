import { DateTime } from 'luxon';
import { ITipoFase } from './tipos-configuracion';

export interface IProyectoPlazos {
  /** Id */
  id: number;
  /** Id de Proyecto */
  proyectoId: number;
  /** Tipo de hito */
  tipoFase: ITipoFase;
  /** Fecha inicio */
  fechaInicio: DateTime;
  /** Fecha fin  */
  fechaFin: DateTime;
  /** Observaciones */
  observaciones: string;
  /** Aviso */
  generaAviso: boolean;
}
