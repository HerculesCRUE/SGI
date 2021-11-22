import { IPaisValidado } from '@core/models/pii/pais-validado';
import { ISolicitudProteccion } from '@core/models/pii/solicitud-proteccion';
import { IPais } from '@core/models/sgo/pais';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { SgiBaseConverter } from '@sgi/framework/core';
import { IPaisValidadoRequest } from './pais-validado-request';

class PaisValidadoRequestConverter extends SgiBaseConverter<IPaisValidadoRequest, IPaisValidado> {

  toTarget(value: IPaisValidadoRequest): IPaisValidado {
    if (!value) {
      return value as unknown as IPaisValidado;
    }

    return {
      id: undefined,
      solicitudProteccion: { id: value.solicitudProteccionId } as ISolicitudProteccion,
      codigoInvencion: value.codigoInvencion,
      fechaValidacion: LuxonUtils.fromBackend(value.fechaValidacion),
      pais: { id: value.paisRef } as IPais
    };
  }

  fromTarget(value: IPaisValidado): IPaisValidadoRequest {
    if (!value) {
      return value as unknown as IPaisValidadoRequest;
    }

    return {
      solicitudProteccionId: value.solicitudProteccion?.id,
      codigoInvencion: value.codigoInvencion,
      fechaValidacion: LuxonUtils.toBackend(value.fechaValidacion),
      paisRef: value.pais.id
    };
  }
}

export const PAIS_VALIDADO_REQUEST_CONVERTER = new PaisValidadoRequestConverter();
