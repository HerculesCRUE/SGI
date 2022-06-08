import { IGrupoLineaInvestigacion } from '@core/models/csp/grupo-linea-investigacion';
import { IGrupoLineaInvestigador } from '@core/models/csp/grupo-linea-investigador';
import { IPersona } from '@core/models/sgp/persona';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { SgiBaseConverter } from '@sgi/framework/core';
import { IGrupoLineaInvestigadorRequest } from './grupo-linea-investigador-request';

class GrupoLineaInvestigadorRequestConverter
  extends SgiBaseConverter<IGrupoLineaInvestigadorRequest, IGrupoLineaInvestigador> {
  toTarget(value: IGrupoLineaInvestigadorRequest): IGrupoLineaInvestigador {
    if (!value) {
      return value as unknown as IGrupoLineaInvestigador;
    }
    return {
      id: value.id,
      persona: value.personaRef ? { id: value.personaRef } as IPersona : undefined,
      grupoLineaInvestigacion: value.grupoLineaInvestigacionId ? { id: value.grupoLineaInvestigacionId } as IGrupoLineaInvestigacion : undefined,
      fechaInicio: LuxonUtils.fromBackend(value.fechaInicio),
      fechaFin: LuxonUtils.fromBackend(value.fechaFin),
    };
  }

  fromTarget(value: IGrupoLineaInvestigador): IGrupoLineaInvestigadorRequest {
    if (!value) {
      return value as unknown as IGrupoLineaInvestigadorRequest;
    }
    return {
      id: value.id,
      personaRef: value.persona.id,
      grupoLineaInvestigacionId: value.grupoLineaInvestigacion.id,
      fechaInicio: LuxonUtils.toBackend(value.fechaInicio),
      fechaFin: LuxonUtils.toBackend(value.fechaFin),
    };
  }
}

export const GRUPO_LINEA_INVESTIGADOR_REQUEST_CONVERTER = new GrupoLineaInvestigadorRequestConverter();
