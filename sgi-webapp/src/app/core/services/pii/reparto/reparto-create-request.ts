export interface IRepartoCreateRequest {
  invencionId: number;
  gastos: IRepartoGastoCreateRequest[];
  ingresos: IRepartoIngresoCreateRequest[];
}

export interface IRepartoGastoCreateRequest {
  invencionGasto: IInvencionGastoCreateRequest;
  importeADeducir: number;
}

export interface IInvencionGastoCreateRequest {
  id: number;
  invencionId: number;
  solicitudProteccionId: number;
  gastoRef: string;
  importePendienteDeducir: number;
}

export interface IRepartoIngresoCreateRequest {
  invencionIngreso: IInvencionIngresoCreateRequest;
  importeARepartir: number;
}

export interface IInvencionIngresoCreateRequest {
  id: number;
  invencionId: number;
  ingresoRef: string;
  importePendienteRepartir: number;
}
