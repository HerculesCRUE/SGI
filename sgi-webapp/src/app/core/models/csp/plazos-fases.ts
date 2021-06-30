import { DateTime } from 'luxon';
import { ITipoFase } from './tipos-configuracion';

export interface IPlazosFases {
  /** Id */
  id: number;
  /** Fecha Inicio */
  fechaInicio: DateTime;
  /** Plazos */
  tipoFase: ITipoFase;
  /** Fecha Fin */
  fechaFin: DateTime;
  /** Observaciones */
  observaciones: string;
  /** Activo */
  activo: boolean;
}
