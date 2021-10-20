import { IPeriodoTitularidad } from '@core/models/pii/periodo-titularidad';
import { IPeriodoTitularidadTitular } from '@core/models/pii/periodo-titularidad-titular';
import { IEmpresa } from '@core/models/sgemp/empresa';
import { SgiBaseConverter } from '@sgi/framework/core';
import { IPeriodoTitularidadTitularResponse } from './periodo-titularidad-titular-response';

class PeriodoTitularidadTitularResponseConverter extends SgiBaseConverter<IPeriodoTitularidadTitularResponse, IPeriodoTitularidadTitular>{
  toTarget(value: IPeriodoTitularidadTitularResponse): IPeriodoTitularidadTitular {
    if (!value) {
      return value as unknown as IPeriodoTitularidadTitular;
    }
    return {
      id: value.id,
      periodoTitularidad: { id: value.periodoTitularidadId } as IPeriodoTitularidad,
      participacion: value.participacion,
      titular: { id: value.titularRef } as IEmpresa
    };
  }
  fromTarget(value: IPeriodoTitularidadTitular): IPeriodoTitularidadTitularResponse {
    if (!value) {
      return value as unknown as IPeriodoTitularidadTitularResponse;
    }
    return {
      id: value.id,
      periodoTitularidadId: value.periodoTitularidad?.id,
      titularRef: value.titular?.id,
      participacion: value.participacion
    };
  }
}

export const PERIODO_TITULARIDAD_TITULAR_RESPONSE_CONVERTER = new PeriodoTitularidadTitularResponseConverter();
