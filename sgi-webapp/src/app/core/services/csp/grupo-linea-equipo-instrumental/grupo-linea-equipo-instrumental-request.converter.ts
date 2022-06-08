import { IGrupoEquipoInstrumental } from '@core/models/csp/grupo-equipo-instrumental';
import { IGrupoLineaEquipoInstrumental } from '@core/models/csp/grupo-linea-equipo-instrumental';
import { IGrupoLineaInvestigacion } from '@core/models/csp/grupo-linea-investigacion';
import { IPersona } from '@core/models/sgp/persona';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { SgiBaseConverter } from '@sgi/framework/core';
import { IGrupoLineaEquipoInstrumentalRequest } from './grupo-linea-equipo-instrumental-request';

class GrupoLineaEquipoInstrumentalRequestConverter
  extends SgiBaseConverter<IGrupoLineaEquipoInstrumentalRequest, IGrupoLineaEquipoInstrumental> {
  toTarget(value: IGrupoLineaEquipoInstrumentalRequest): IGrupoLineaEquipoInstrumental {
    if (!value) {
      return value as unknown as IGrupoLineaEquipoInstrumental;
    }
    return {
      id: value.id,
      grupoEquipoInstrumental: value.grupoEquipoInstrumentalId ? { id: value.grupoEquipoInstrumentalId } as IGrupoEquipoInstrumental : undefined,
      grupoLineaInvestigacion: value.grupoLineaInvestigacionId ? { id: value.grupoLineaInvestigacionId } as IGrupoLineaInvestigacion : undefined,
    };
  }

  fromTarget(value: IGrupoLineaEquipoInstrumental): IGrupoLineaEquipoInstrumentalRequest {
    if (!value) {
      return value as unknown as IGrupoLineaEquipoInstrumentalRequest;
    }
    return {
      id: value.id,
      grupoEquipoInstrumentalId: value.grupoEquipoInstrumental.id,
      grupoLineaInvestigacionId: value.grupoLineaInvestigacion.id,
    };
  }
}

export const GRUPO_LINEA_EQUIPO_INSTRUMENTAL_REQUEST_CONVERTER = new GrupoLineaEquipoInstrumentalRequestConverter();
