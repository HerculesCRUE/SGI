import { Estado } from '@core/models/pii/invencion-ingreso';

export interface IInvencionIngresoResponse {
  id: number;
  invencionId: number;
  ingresoRef: string;
  importePendienteRepartir: number;
  estado: Estado;
}