import { TipoSeguimiento } from '@core/enums/tipo-seguimiento';
import { I18nFieldValueResponse } from '@core/i18n/i18n-field-response';

export interface IConvocatoriaPeriodoSeguimientoCientificoResponse {
  id: number;
  numPeriodo: number;
  mesInicial: number;
  mesFinal: number;
  fechaInicioPresentacion: string;
  fechaFinPresentacion: string;
  tipoSeguimiento: TipoSeguimiento;
  observaciones: I18nFieldValueResponse[];
  convocatoriaId: number;
}
