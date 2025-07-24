import { TipoSeguimiento } from '@core/enums/tipo-seguimiento';
import { I18nFieldValue } from '@core/i18n/i18n-field';
import { DateTime } from 'luxon';

export interface IConvocatoriaPeriodoSeguimientoCientifico {
  id: number;
  numPeriodo: number;
  mesInicial: number;
  mesFinal: number;
  fechaInicioPresentacion: DateTime;
  fechaFinPresentacion: DateTime;
  tipoSeguimiento: TipoSeguimiento;
  observaciones: I18nFieldValue[];
  convocatoriaId: number;
}
