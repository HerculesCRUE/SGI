import { IInvencion } from '@core/models/pii/invencion';
import { IPeriodoTitularidad } from '@core/models/pii/periodo-titularidad';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { SgiBaseConverter } from '@sgi/framework/core';
import { IPeriodoTitularidadRequest } from './periodo-titularidad-request';

class PeriodoTitularidadRequestConverter extends SgiBaseConverter<IPeriodoTitularidadRequest, IPeriodoTitularidad>{
  toTarget(value: IPeriodoTitularidadRequest): IPeriodoTitularidad {
    if (!value) {
      return value as unknown as IPeriodoTitularidad;
    }
    return {
      id: undefined,
      invencion: { id: value.invencionId } as IInvencion,
      fechaInicio: LuxonUtils.fromBackend(value.fechaInicio),
      fechaFin: LuxonUtils.fromBackend(value.fechaFin),
    };
  }
  fromTarget(value: IPeriodoTitularidad): IPeriodoTitularidadRequest {
    if (!value) {
      return value as unknown as IPeriodoTitularidadRequest;
    }
    return {
      invencionId: value.invencion.id,
      fechaInicio: LuxonUtils.toBackend(value.fechaInicio),
      fechaFin: LuxonUtils.toBackend(value.fechaFin),
      fechaFinPrevious: LuxonUtils.toBackend(value?.fechaFinPrevious),
    };
  }
}

export const PERIODO_TITULARIDAD_REQUEST_CONVERTER = new PeriodoTitularidadRequestConverter();
