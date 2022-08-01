import { IProyectoFaseAviso } from '@core/services/csp/proyecto-fase/proyecto-fase-aviso';
import { DateTime } from 'luxon';
import { ITipoFase } from './tipos-configuracion';

export interface IProyectoFase {
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
  /** Aviso 1 */
  aviso1: IProyectoFaseAviso;
  /** Aviso 2 */
  aviso2: IProyectoFaseAviso;
}
