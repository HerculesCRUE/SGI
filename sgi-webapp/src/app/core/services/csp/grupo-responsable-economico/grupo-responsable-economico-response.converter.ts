import { IGrupo } from '@core/models/csp/grupo';
import { IGrupoResponsableEconomico } from '@core/models/csp/grupo-responsable-economico';
import { IPersona } from '@core/models/sgp/persona';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { SgiBaseConverter } from '@sgi/framework/core';
import { IGrupoResponsableEconomicoResponse } from './grupo-responsable-economico-response';

class GrupoResponsableEconomicoResponseConverter
  extends SgiBaseConverter<IGrupoResponsableEconomicoResponse, IGrupoResponsableEconomico> {
  toTarget(value: IGrupoResponsableEconomicoResponse): IGrupoResponsableEconomico {
    if (!value) {
      return value as unknown as IGrupoResponsableEconomico;
    }
    return {
      id: value.id,
      persona: value.personaRef ? { id: value.personaRef } as IPersona : undefined,
      grupo: value.grupoId ? { id: value.grupoId } as IGrupo : undefined,
      fechaInicio: LuxonUtils.fromBackend(value.fechaInicio),
      fechaFin: LuxonUtils.fromBackend(value.fechaFin),
    };
  }

  fromTarget(value: IGrupoResponsableEconomico): IGrupoResponsableEconomicoResponse {
    if (!value) {
      return value as unknown as IGrupoResponsableEconomicoResponse;
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

export const GRUPO_RESPONSABLE_ECONOMICO_RESPONSE_CONVERTER = new GrupoResponsableEconomicoResponseConverter();
