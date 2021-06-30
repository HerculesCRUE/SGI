import { TipoSeguimiento } from '@core/enums/tipo-seguimiento';
import { DateTime } from 'luxon';

export interface IConvocatoriaPeriodoSeguimientoCientifico {
  id: number;
  numPeriodo: number;
  mesInicial: number;
  mesFinal: number;
  fechaInicioPresentacion: DateTime;
  fechaFinPresentacion: DateTime;
  tipoSeguimiento: TipoSeguimiento;
  observaciones: string;
  convocatoriaId: number;
}
