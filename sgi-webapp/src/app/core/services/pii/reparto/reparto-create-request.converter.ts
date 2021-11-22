import { IInvencionGasto } from '@core/models/pii/invencion-gasto';
import { IInvencionIngreso } from '@core/models/pii/invencion-ingreso';
import { IRepartoCreate } from '@core/models/pii/reparto';
import { IRepartoGasto } from '@core/models/pii/reparto-gasto';
import { IRepartoIngreso } from '@core/models/pii/reparto-ingreso';
import { SgiBaseConverter } from '@sgi/framework/core';
import {
  IInvencionGastoCreateRequest, IInvencionIngresoCreateRequest, IRepartoCreateRequest,
  IRepartoGastoCreateRequest, IRepartoIngresoCreateRequest
} from './reparto-create-request';

class RepartoCreateRequestConverter extends SgiBaseConverter<IRepartoCreateRequest, IRepartoCreate> {

  toTarget(value: IRepartoCreateRequest): IRepartoCreate {
    if (!value) {
      return value as unknown as IRepartoCreate;
    }

    return {} as IRepartoCreate;
  }

  fromTarget(value: IRepartoCreate): IRepartoCreateRequest {
    if (!value) {
      return value as unknown as IRepartoCreateRequest;
    }

    return {
      invencionId: value.invencion?.id,
      gastos: value.gastos.map(gasto => this.convertRepartoGasto(gasto)),
      ingresos: value.ingresos.map(ingreso => this.convertRepartoIngreso(ingreso)),
    };
  }

  private convertRepartoGasto(repartoGasto: IRepartoGasto): IRepartoGastoCreateRequest {
    return {
      invencionGasto: this.convertInvencionGasto(repartoGasto.invencionGasto),
      importeADeducir: repartoGasto.importeADeducir
    };
  }

  private convertInvencionGasto(invencionGasto: IInvencionGasto): IInvencionGastoCreateRequest {
    return {
      id: invencionGasto.id,
      invencionId: invencionGasto.invencion?.id,
      gastoRef: invencionGasto.gasto?.id,
      solicitudProteccionId: invencionGasto.solicitudProteccion?.id,
      importePendienteDeducir: invencionGasto.importePendienteDeducir
    };
  }

  private convertRepartoIngreso(repartoIngreso: IRepartoIngreso): IRepartoIngresoCreateRequest {
    return {
      invencionIngreso: this.convertInvencionIngreso(repartoIngreso.invencionIngreso),
      importeARepartir: repartoIngreso.importeARepartir
    };
  }

  private convertInvencionIngreso(invencionIngreso: IInvencionIngreso): IInvencionIngresoCreateRequest {
    return {
      id: invencionIngreso.id,
      invencionId: invencionIngreso.invencion?.id,
      ingresoRef: invencionIngreso.ingreso.id,
      importePendienteRepartir: invencionIngreso.importePendienteRepartir
    };
  }
}

export const REPARTO_CREATE_REQUEST_CONVERTER = new RepartoCreateRequestConverter();
