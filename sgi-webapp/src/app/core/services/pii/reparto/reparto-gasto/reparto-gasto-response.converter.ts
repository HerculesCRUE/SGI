import { IInvencionGasto } from '@core/models/pii/invencion-gasto';
import { IReparto } from '@core/models/pii/reparto';
import { IRepartoGasto } from '@core/models/pii/reparto-gasto';
import { SgiBaseConverter } from '@sgi/framework/core';
import { IRepartoGastoResponse } from './reparto-gasto-response';

class RepartoGastoResponseConverter extends SgiBaseConverter<IRepartoGastoResponse, IRepartoGasto>{
  toTarget(value: IRepartoGastoResponse): IRepartoGasto {
    if (!value) {
      return value as unknown as IRepartoGasto;
    }
    return {
      id: value.id,
      reparto: { id: value.repartoId } as IReparto,
      invencionGasto: { id: value.invencionGastoId } as IInvencionGasto,
      importeADeducir: value.importeADeducir
    };
  }

  fromTarget(value: IRepartoGasto): IRepartoGastoResponse {
    if (!value) {
      return value as unknown as IRepartoGastoResponse;
    }
    return {
      id: value.id,
      repartoId: value.reparto?.id,
      invencionGastoId: value.invencionGasto?.id,
      importeADeducir: value.importeADeducir
    };
  }
}

export const REPARTO_GASTO_RESPONSE_CONVERTER = new RepartoGastoResponseConverter();
