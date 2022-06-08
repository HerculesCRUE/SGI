import { IConvocatoriaBaremacion } from '@core/models/prc/convocatoria-baremacion';
import { IRango } from '@core/models/prc/rango';
import { SgiBaseConverter } from '@sgi/framework/core';
import { IRangoResponse } from './rango-response';

class RangoResponseConverter extends SgiBaseConverter<IRangoResponse, IRango>{
  toTarget(value: IRangoResponse): IRango {
    if (!value) {
      return value as unknown as IRango;
    }
    return {
      id: value.id,
      tipoRango: value.tipoRango,
      tipoTemporalidad: value.tipoTemporalidad,
      desde: value.desde,
      hasta: value.hasta,
      puntos: value.puntos,
      convocatoriaBaremacion: value.convocatoriaBaremacionId ? { id: value.convocatoriaBaremacionId } as IConvocatoriaBaremacion : null
    };
  }
  fromTarget(value: IRango): IRangoResponse {
    if (!value) {
      return value as unknown as IRangoResponse;
    }
    return {
      id: value.id,
      tipoRango: value.tipoRango,
      tipoTemporalidad: value.tipoTemporalidad,
      desde: value.desde,
      hasta: value.hasta,
      puntos: value.puntos,
      convocatoriaBaremacionId: value.convocatoriaBaremacion?.id
    };
  }
}

export const RANGO_RESPONSE_CONVERTER = new RangoResponseConverter();
