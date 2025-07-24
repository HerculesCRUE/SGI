import { TipoSeguimiento } from '@core/enums/tipo-seguimiento';
import { I18nFieldValue } from '@core/i18n/i18n-field';
import { DateTime } from 'luxon';

export interface IProyectoPeriodoSeguimiento {
  id: number;
  proyectoId: number;
  numPeriodo: number;
  fechaInicio: DateTime;
  fechaFin: DateTime;
  fechaInicioPresentacion: DateTime;
  fechaFinPresentacion: DateTime;
  tipoSeguimiento: TipoSeguimiento;
  observaciones: I18nFieldValue[];
  convocatoriaPeriodoSeguimientoId: number;
  fechaPresentacionDocumentacion: DateTime;
}
