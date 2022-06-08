import { TipoRango, TipoTemporalidad } from '@core/models/prc/rango';

export interface IRangoRequest {
  tipoRango: TipoRango;
  tipoTemporalidad: TipoTemporalidad;
  desde: number;
  hasta: number;
  puntos: number;
  convocatoriaBaremacionId: number;
}
