import { I18nFieldValue } from '@core/i18n/i18n-field';
import { DateTime } from 'luxon';

export interface IProyectoSocioPeriodoJustificacion {
  id: number;
  proyectoSocioId: number;
  numPeriodo: number;
  fechaInicio: DateTime;
  fechaFin: DateTime;
  fechaInicioPresentacion: DateTime;
  fechaFinPresentacion: DateTime;
  observaciones: I18nFieldValue[];
  documentacionRecibida: boolean;
  fechaRecepcion: DateTime;
  importeJustificado: number;
}
