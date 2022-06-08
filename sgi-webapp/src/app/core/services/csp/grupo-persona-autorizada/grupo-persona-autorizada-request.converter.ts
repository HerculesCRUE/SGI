import { IGrupo } from '@core/models/csp/grupo';
import { IGrupoPersonaAutorizada } from '@core/models/csp/grupo-persona-autorizada';
import { IPersona } from '@core/models/sgp/persona';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { SgiBaseConverter } from '@sgi/framework/core';
import { IGrupoPersonaAutorizadaRequest } from './grupo-persona-autorizada-request';

class GrupoPersonaAutorizadaRequestConverter
  extends SgiBaseConverter<IGrupoPersonaAutorizadaRequest, IGrupoPersonaAutorizada> {
  toTarget(value: IGrupoPersonaAutorizadaRequest): IGrupoPersonaAutorizada {
    if (!value) {
      return value as unknown as IGrupoPersonaAutorizada;
    }
    return {
      id: value.id,
      persona: value.personaRef ? { id: value.personaRef } as IPersona : undefined,
      grupo: value.grupoId ? { id: value.grupoId } as IGrupo : undefined,
      fechaInicio: LuxonUtils.fromBackend(value.fechaInicio),
      fechaFin: LuxonUtils.fromBackend(value.fechaFin),
    };
  }

  fromTarget(value: IGrupoPersonaAutorizada): IGrupoPersonaAutorizadaRequest {
    if (!value) {
      return value as unknown as IGrupoPersonaAutorizadaRequest;
    }
    return {
      id: value.id,
      personaRef: value.persona.id,
      grupoId: value.grupo.id,
      fechaInicio: LuxonUtils.toBackend(value.fechaInicio),
      fechaFin: LuxonUtils.toBackend(value.fechaFin),
    };
  }
}

export const GRUPO_PERSONA_AUTORIZADA_REQUEST_CONVERTER = new GrupoPersonaAutorizadaRequestConverter();
