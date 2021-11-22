import { IInvencionGasto } from '@core/models/pii/invencion-gasto';
import { IReparto } from '@core/models/pii/reparto';
import { IRepartoGasto } from '@core/models/pii/reparto-gasto';
import { SgiBaseConverter } from '@sgi/framework/core';
import { IRepartoGastoRequest } from './reparto-gasto-request';

class RepartoGastoRequestConverter extends SgiBaseConverter<IRepartoGastoRequest, IRepartoGasto> {

  toTarget(value: IRepartoGastoRequest): IRepartoGasto {
    if (!value) {
      return value as unknown as IRepartoGasto;
    }

    return {
      id: undefined,
      reparto: { id: value.repartoId } as IReparto,
      invencionGasto: { id: value.invencionGastoId } as IInvencionGasto,
      importeADeducir: value.importeADeducir
    };
  }

  fromTarget(value: IRepartoGasto): IRepartoGastoRequest {
    if (!value) {
      return value as unknown as IRepartoGastoRequest;
    }

    return {
      repartoId: value.reparto?.id,
      invencionGastoId: value.invencionGasto?.id,
      importeADeducir: value.importeADeducir
    };
  }
}

export const REPARTO_GASTO_REQUEST_CONVERTER = new RepartoGastoRequestConverter();
