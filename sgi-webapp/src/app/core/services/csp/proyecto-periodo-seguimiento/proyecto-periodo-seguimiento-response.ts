import { TipoSeguimiento } from '@core/enums/tipo-seguimiento';
import { I18nFieldValueResponse } from '@core/i18n/i18n-field-response';

export interface IProyectoPeriodoSeguimientoResponse {
  id: number;
  proyectoId: number;
  numPeriodo: number;
  fechaInicio: string;
  fechaFin: string;
  fechaInicioPresentacion: string;
  fechaFinPresentacion: string;
  tipoSeguimiento: TipoSeguimiento;
  observaciones: I18nFieldValueResponse[];
  convocatoriaPeriodoSeguimientoId: number;
  fechaPresentacionDocumentacion: string;
}
