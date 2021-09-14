import { TipoJustificacion } from '@core/enums/tipo-justificacion';
import { DateTime } from 'luxon';

export interface IConvocatoriaPeriodoJustificacion {
  /** Id */
  id: number;
  /** Id de Convocatoria */
  convocatoriaId: number;
  /** Num Periodo */
  numPeriodo: number;
  /** Mes inicial */
  mesInicial: number;
  /** Mes final */
  mesFinal: number;
  /** Fecha inicio */
  fechaInicioPresentacion: DateTime;
  /** Fecha fin */
  fechaFinPresentacion: DateTime;
  /** Observaciones */
  observaciones: string;
  /** Tipo justificacion */
  tipo: TipoJustificacion;
}


