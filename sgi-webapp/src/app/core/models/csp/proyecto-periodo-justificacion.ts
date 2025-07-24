import { TipoJustificacion } from '@core/enums/tipo-justificacion';
import { I18nFieldValue } from '@core/i18n/i18n-field';
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
  observaciones: I18nFieldValue[];
  convocatoriaPeriodoJustificacionId: number;
  fechaPresentacionJustificacion: DateTime;
  identificadorJustificacion: string;
  estado: IEstadoProyectoPeriodoJustificacion;
}
