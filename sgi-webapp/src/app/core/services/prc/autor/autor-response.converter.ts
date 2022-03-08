import { IAutor } from '@core/models/prc/autor';
import { IProduccionCientifica } from '@core/models/prc/produccion-cientifica';
import { IPersona } from '@core/models/sgp/persona';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { SgiBaseConverter } from '@sgi/framework/core';
import { IAutorResponse } from './autor-response';

class AutorResponseConverter extends SgiBaseConverter<IAutorResponse, IAutor>{
  toTarget(value: IAutorResponse): IAutor {
    if (!value) {
      return value as unknown as IAutor;
    }
    return {
      id: value.id,
      apellidos: value.apellidos,
      fechaFin: LuxonUtils.fromBackend(value.fechaFin),
      fechaInicio: LuxonUtils.fromBackend(value.fechaInicio),
      firma: value.firma,
      ip: value.ip,
      nombre: value.nombre,
      orcidId: value.orcidId,
      orden: value.orden,
      persona: value.personaRef ?
        { id: value.personaRef } as IPersona : null,
      produccionCientifica: value.produccionCientificaId ?
        { id: value.produccionCientificaId } as IProduccionCientifica : null,

    };
  }
  fromTarget(value: IAutor): IAutorResponse {
    if (!value) {
      return value as unknown as IAutorResponse;
    }
    return {
      id: value.id,
      apellidos: value.apellidos,
      fechaFin: LuxonUtils.toBackend(value.fechaFin),
      fechaInicio: LuxonUtils.toBackend(value.fechaInicio),
      firma: value.firma,
      ip: value.ip,
      nombre: value.nombre,
      orcidId: value.orcidId,
      orden: value.orden,
      personaRef: value.persona?.id,
      produccionCientificaId: value.produccionCientifica?.id,
    };
  }
}

export const AUTOR_RESPONSE_CONVERTER = new AutorResponseConverter();
