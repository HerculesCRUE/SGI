import { IConvocatoriaBaremacion } from '@core/models/prc/convocatoria-baremacion';
import { IRango } from '@core/models/prc/rango';
import { SgiBaseConverter } from '@sgi/framework/core';
import { IRangoRequest } from './rango-request';

class RangoRequestConverter extends SgiBaseConverter<IRangoRequest, IRango>{
  toTarget(value: IRangoRequest): IRango {
    if (!value) {
      return value as unknown as IRango;
    }
    return {
      id: undefined,
      tipoRango: value.tipoRango,
      tipoTemporalidad: value.tipoTemporalidad,
      desde: value.desde,
      hasta: value.hasta,
      puntos: value.puntos,
      convocatoriaBaremacion: value.convocatoriaBaremacionId ? { id: value.convocatoriaBaremacionId } as IConvocatoriaBaremacion : null
    };
  }
  fromTarget(value: IRango): IRangoRequest {
    if (!value) {
      return value as unknown as IRangoRequest;
    }
    return {
      tipoRango: value.tipoRango,
      tipoTemporalidad: value.tipoTemporalidad,
      desde: value.desde,
      hasta: value.hasta,
      puntos: value.puntos,
      convocatoriaBaremacionId: value.convocatoriaBaremacion?.id
    };
  }
}

export const RANGO_REQUEST_CONVERTER = new RangoRequestConverter();
