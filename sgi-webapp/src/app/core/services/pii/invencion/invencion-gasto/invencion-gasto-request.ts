import { Estado } from '@core/models/pii/invencion-gasto';

export interface IInvencionGastoRequest {
  invencionId: number;
  solicitudProteccionId: number;
  gastoRef: string;
  importePendienteDeducir: number;
  estado: Estado;
}
