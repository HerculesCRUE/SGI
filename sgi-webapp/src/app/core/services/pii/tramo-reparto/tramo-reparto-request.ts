import { Tipo } from '@core/models/pii/tramo-reparto';

export interface ITramoRepartoRequest {
  desde: number;
  hasta: number;
  porcentajeUniversidad: number;
  porcentajeInventores: number;
  tipo: Tipo;
}