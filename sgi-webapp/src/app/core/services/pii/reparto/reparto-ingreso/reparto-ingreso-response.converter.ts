import { IInvencionIngreso } from '@core/models/pii/invencion-ingreso';
import { IReparto } from '@core/models/pii/reparto';
import { IRepartoIngreso } from '@core/models/pii/reparto-ingreso';
import { SgiBaseConverter } from '@sgi/framework/core';
import { IRepartoIngresoResponse } from './reparto-ingreso-response';

class RepartoIngresoResponseConverter extends SgiBaseConverter<IRepartoIngresoResponse, IRepartoIngreso>{
  toTarget(value: IRepartoIngresoResponse): IRepartoIngreso {
    if (!value) {
      return value as unknown as IRepartoIngreso;
    }
    return {
      id: value.id,
      reparto: { id: value.repartoId } as IReparto,
      invencionIngreso: { id: value.invencionIngresoId } as IInvencionIngreso,
      importeARepartir: value.importeARepartir
    };
  }

  fromTarget(value: IRepartoIngreso): IRepartoIngresoResponse {
    if (!value) {
      return value as unknown as IRepartoIngresoResponse;
    }
    return {
      id: value.id,
      repartoId: value.reparto?.id,
      invencionIngresoId: value.invencionIngreso?.id,
      importeARepartir: value.importeARepartir
    };
  }
}

export const REPARTO_INGRESO_RESPONSE_CONVERTER = new RepartoIngresoResponseConverter();
