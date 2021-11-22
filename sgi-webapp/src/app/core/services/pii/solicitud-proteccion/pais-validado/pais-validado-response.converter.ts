
import { IPaisValidado } from '@core/models/pii/pais-validado';
import { ISolicitudProteccion } from '@core/models/pii/solicitud-proteccion';
import { IPais } from '@core/models/sgo/pais';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { SgiBaseConverter } from '@sgi/framework/core';
import { IPaisValidadoResponse } from './pais-validado-response';


class PaisValidadoResponseConverter extends SgiBaseConverter<IPaisValidadoResponse, IPaisValidado> {

  toTarget(value: IPaisValidadoResponse): IPaisValidado {
    if (!value) {
      return value as unknown as IPaisValidado;
    }

    return {
      id: value.id,
      solicitudProteccion: { id: value.solicitudProteccionId } as ISolicitudProteccion,
      codigoInvencion: value.codigoInvencion,
      fechaValidacion: LuxonUtils.fromBackend(value.fechaValidacion),
      pais: { id: value.paisRef } as IPais
    };
  }

  fromTarget(value: IPaisValidado): IPaisValidadoResponse {
    if (!value) {
      return value as unknown as IPaisValidadoResponse;
    }

    return {
      id: value.id,
      solicitudProteccionId: value.solicitudProteccion?.id,
      codigoInvencion: value.codigoInvencion,
      fechaValidacion: LuxonUtils.toBackend(value.fechaValidacion),
      paisRef: value.pais.id
    };
  }
}

export const PAIS_VALIDADO_RESPONSE_CONVERTER = new PaisValidadoResponseConverter();
