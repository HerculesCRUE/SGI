import { IInvencion } from '@core/models/pii/invencion';
import { IPeriodoTitularidad } from '@core/models/pii/periodo-titularidad';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { SgiBaseConverter } from '@sgi/framework/core';
import { IPeriodoTitularidadResponse } from './periodo-titularidad-response';

class PeriodoTitularidadResponseConverter extends SgiBaseConverter<IPeriodoTitularidadResponse, IPeriodoTitularidad>{
  toTarget(value: IPeriodoTitularidadResponse): IPeriodoTitularidad {
    if (!value) {
      return value as unknown as IPeriodoTitularidad;
    }
    return {
      id: value.id,
      invencion: { id: value.invencionId } as IInvencion,
      fechaInicio: LuxonUtils.fromBackend(value.fechaInicio),
      fechaFin: LuxonUtils.fromBackend(value.fechaFin),
    };
  }
  fromTarget(value: IPeriodoTitularidad): IPeriodoTitularidadResponse {
    if (!value) {
      return value as unknown as IPeriodoTitularidadResponse;
    }
    return {
      id: value.id,
      invencionId: value.invencion?.id,
      fechaInicio: LuxonUtils.toBackend(value.fechaInicio),
      fechaFin: LuxonUtils.toBackend(value.fechaFin)
    };
  }
}

export const PERIODO_TITULARIDAD_RESPONSE_CONVERTER = new PeriodoTitularidadResponseConverter();
