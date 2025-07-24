import { I18nFieldValue } from '@core/i18n/i18n-field';
import { DateTime } from 'luxon';

export interface ISolicitudProyectoSocioPeriodoJustificacion {
  id: number;
  solicitudProyectoSocioId: number;
  numPeriodo: number;
  mesInicial: number;
  mesFinal: number;
  fechaInicio: DateTime;
  fechaFin: DateTime;
  observaciones: I18nFieldValue[];
}
