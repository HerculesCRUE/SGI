import { IInvencion } from '@core/models/pii/invencion';
import { IReparto } from '@core/models/pii/reparto';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { SgiBaseConverter } from '@sgi/framework/core';
import { IRepartoRequest } from './reparto-request';

class RepartoRequestConverter extends SgiBaseConverter<IRepartoRequest, IReparto> {

  toTarget(value: IRepartoRequest): IReparto {
    if (!value) {
      return value as unknown as IReparto;
    }

    return {
      id: undefined,
      invencion: { id: value.invencionId } as IInvencion,
      fecha: LuxonUtils.fromBackend(value.fecha),
      importeUniversidad: value.importeUniversidad,
      importeEquipoInventor: value.importeEquipoInventor,
      estado: value.estado
    };
  }

  fromTarget(value: IReparto): IRepartoRequest {
    if (!value) {
      return value as unknown as IRepartoRequest;
    }

    return {
      invencionId: value.invencion?.id,
      fecha: LuxonUtils.toBackend(value.fecha),
      importeUniversidad: value.importeUniversidad,
      importeEquipoInventor: value.importeEquipoInventor,
      estado: value.estado
    };
  }
}

export const REPARTO_REQUEST_CONVERTER = new RepartoRequestConverter();
