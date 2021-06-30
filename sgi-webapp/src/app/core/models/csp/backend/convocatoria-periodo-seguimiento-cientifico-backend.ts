import { TipoSeguimiento } from '@core/enums/tipo-seguimiento';

export interface IConvocatoriaPeriodoSeguimientoCientificoBackend {
  id: number;
  numPeriodo: number;
  mesInicial: number;
  mesFinal: number;
  fechaInicioPresentacion: string;
  fechaFinPresentacion: string;
  tipoSeguimiento: TipoSeguimiento;
  observaciones: string;
  convocatoriaId: number;
}
