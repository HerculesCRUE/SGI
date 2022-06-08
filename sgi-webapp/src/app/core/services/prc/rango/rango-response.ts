import { TipoRango, TipoTemporalidad } from '@core/models/prc/rango';

export interface IRangoResponse {
  id: number;
  tipoRango: TipoRango;
  tipoTemporalidad: TipoTemporalidad;
  desde: number;
  hasta: number;
  puntos: number;
  convocatoriaBaremacionId: number;
}
