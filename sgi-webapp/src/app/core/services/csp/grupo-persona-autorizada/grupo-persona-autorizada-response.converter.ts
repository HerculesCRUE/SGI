import { IGrupo } from '@core/models/csp/grupo';
import { IGrupoPersonaAutorizada } from '@core/models/csp/grupo-persona-autorizada';
import { IPersona } from '@core/models/sgp/persona';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { SgiBaseConverter } from '@sgi/framework/core';
import { IGrupoPersonaAutorizadaResponse } from './grupo-persona-autorizada-response';

class GrupoPersonaAutorizadaResponseConverter
  extends SgiBaseConverter<IGrupoPersonaAutorizadaResponse, IGrupoPersonaAutorizada> {
  toTarget(value: IGrupoPersonaAutorizadaResponse): IGrupoPersonaAutorizada {
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

  fromTarget(value: IGrupoPersonaAutorizada): IGrupoPersonaAutorizadaResponse {
    if (!value) {
      return value as unknown as IGrupoPersonaAutorizadaResponse;
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

export const GRUPO_PERSONA_AUTORIZADA_RESPONSE_CONVERTER = new GrupoPersonaAutorizadaResponseConverter();
