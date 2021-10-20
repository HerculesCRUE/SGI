import { Estado } from '@core/models/pii/invencion-ingreso';

export interface IInvencionIngresoRequest {
  invencionId: number;
  ingresoRef: string;
  importePendienteRepartir: number;
  estado: Estado;
}