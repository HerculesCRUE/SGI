import { Estado } from '@core/models/pii/invencion-gasto';

export interface IInvencionGastoResponse {
  id: number;
  invencionId: number;
  solicitudProteccionId: number;
  gastoRef: string;
  importePendienteDeducir: number;
  estado: Estado;
}
