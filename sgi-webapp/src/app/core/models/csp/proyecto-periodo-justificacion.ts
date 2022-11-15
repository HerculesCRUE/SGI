import { TipoJustificacion } from '@core/enums/tipo-justificacion';
import { DateTime } from 'luxon';
import { IEstadoProyectoPeriodoJustificacion } from './estado-proyecto-periodo-justificacion';
import { IProyecto } from './proyecto';

export interface IProyectoPeriodoJustificacion {
  id: number;
  proyecto: IProyecto;
  numPeriodo: number;
  fechaInicio: DateTime;
  fechaFin: DateTime;
  fechaInicioPresentacion: DateTime;
  fechaFinPresentacion: DateTime;
  tipoJustificacion: TipoJustificacion;
  observaciones: string;
  convocatoriaPeriodoJustificacionId: number;
  fechaPresentacionJustificacion: DateTime;
  identificadorJustificacion: string;
  estado: IEstadoProyectoPeriodoJustificacion;
}
