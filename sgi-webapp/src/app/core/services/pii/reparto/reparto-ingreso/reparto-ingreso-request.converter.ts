import { IInvencionIngreso } from '@core/models/pii/invencion-ingreso';
import { IReparto } from '@core/models/pii/reparto';
import { IRepartoIngreso } from '@core/models/pii/reparto-ingreso';
import { SgiBaseConverter } from '@sgi/framework/core';
import { IRepartoIngresoRequest } from './reparto-ingreso-request';

class RepartoIngresoRequestConverter extends SgiBaseConverter<IRepartoIngresoRequest, IRepartoIngreso> {

  toTarget(value: IRepartoIngresoRequest): IRepartoIngreso {
    if (!value) {
      return value as unknown as IRepartoIngreso;
    }

    return {
      id: undefined,
      reparto: { id: value.repartoId } as IReparto,
      invencionIngreso: { id: value.invencionIngresoId } as IInvencionIngreso,
      importeARepartir: value.importeARepartir
    };
  }

  fromTarget(value: IRepartoIngreso): IRepartoIngresoRequest {
    if (!value) {
      return value as unknown as IRepartoIngresoRequest;
    }

    return {
      repartoId: value.reparto?.id,
      invencionIngresoId: value.invencionIngreso?.id,
      importeARepartir: value.importeARepartir
    };
  }
}

export const REPARTO_INGRESO_REQUEST_CONVERTER = new RepartoIngresoRequestConverter();
