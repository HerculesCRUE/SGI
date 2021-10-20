import { IPeriodoTitularidad } from '@core/models/pii/periodo-titularidad';
import { IPeriodoTitularidadTitular } from '@core/models/pii/periodo-titularidad-titular';
import { IEmpresa } from '@core/models/sgemp/empresa';
import { SgiBaseConverter } from '@sgi/framework/core';
import { IPeriodoTitularidadTitularRequest } from './periodo-titularidad-titular-request';

class PeriodoTitularidadTitularRequestConverter extends SgiBaseConverter<IPeriodoTitularidadTitularRequest, IPeriodoTitularidadTitular>{
  toTarget(value: IPeriodoTitularidadTitularRequest): IPeriodoTitularidadTitular {
    if (!value) {
      return value as unknown as IPeriodoTitularidadTitular;
    }
    return {
      id: undefined,
      participacion: value.participacion,
      periodoTitularidad: {} as IPeriodoTitularidad,
      titular: { id: value.titularRef } as IEmpresa
    };
  }
  fromTarget(value: IPeriodoTitularidadTitular): IPeriodoTitularidadTitularRequest {
    if (!value) {
      return value as unknown as IPeriodoTitularidadTitularRequest;
    }
    return {
      titularRef: value.titular?.id,
      participacion: value.participacion

    };
  }
}

export const PERIODO_TITULARIDAD_TITULAR_REQUEST_CONVERTER = new PeriodoTitularidadTitularRequestConverter();
