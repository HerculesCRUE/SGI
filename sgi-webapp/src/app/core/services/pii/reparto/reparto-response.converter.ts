import { IInvencion } from '@core/models/pii/invencion';
import { IReparto } from '@core/models/pii/reparto';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { SgiBaseConverter } from '@sgi/framework/core';
import { IRepartoResponse } from './reparto-response';

class RepartoResponseConverter extends SgiBaseConverter<IRepartoResponse, IReparto>{
  toTarget(value: IRepartoResponse): IReparto {
    if (!value) {
      return value as unknown as IReparto;
    }
    return {
      id: value.id,
      invencion: { id: +value.invencionId } as IInvencion,
      fecha: LuxonUtils.fromBackend(value.fecha),
      importeUniversidad: value.importeUniversidad,
      importeEquipoInventor: value.importeEquipoInventor,
      estado: value.estado
    };
  }

  fromTarget(value: IReparto): IRepartoResponse {
    if (!value) {
      return value as unknown as IRepartoResponse;
    }
    return {
      id: value.id,
      invencionId: value.invencion?.id,
      fecha: LuxonUtils.toBackend(value.fecha),
      importeUniversidad: value.importeUniversidad,
      importeEquipoInventor: value.importeEquipoInventor,
      estado: value.estado
    };
  }
}

export const REPARTO_RESPONSE_CONVERTER = new RepartoResponseConverter();
