import { TipoJustificacion } from '@core/enums/tipo-justificacion';
import { DateTime } from 'luxon';

export interface IProyectoPeriodoJustificacion {
  id: number;
  proyecto: {
    id: number
  };
  numPeriodo: number;
  fechaInicio: DateTime;
  fechaFin: DateTime;
  fechaInicioPresentacion: DateTime;
  fechaFinPresentacion: DateTime;
  tipoJustificacion: TipoJustificacion;
  observaciones: string;
  convocatoriaPeriodoJustificacionId: number;
}
