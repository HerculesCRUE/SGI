import { IGrupo } from '@core/models/csp/grupo';
import { IGrupoEspecialInvestigacion } from '@core/models/csp/grupo-especial-investigacion';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { SgiBaseConverter } from '@sgi/framework/core';
import { IGrupoEspecialInvestigacionResponse } from './grupo-especial-investigacion-response';

class GrupoEspecialInvestigacionResponseConverter
  extends SgiBaseConverter<IGrupoEspecialInvestigacionResponse, IGrupoEspecialInvestigacion> {
  toTarget(value: IGrupoEspecialInvestigacionResponse): IGrupoEspecialInvestigacion {
    if (!value) {
      return value as unknown as IGrupoEspecialInvestigacion;
    }
    return {
      id: value.id,
      grupo: value.grupoId ? { id: value.grupoId } as IGrupo : undefined,
      fechaInicio: LuxonUtils.fromBackend(value.fechaInicio),
      fechaFin: LuxonUtils.fromBackend(value.fechaFin),
      especialInvestigacion: value.especialInvestigacion
    };
  }

  fromTarget(value: IGrupoEspecialInvestigacion): IGrupoEspecialInvestigacionResponse {
    if (!value) {
      return value as unknown as IGrupoEspecialInvestigacionResponse;
    }
    return {
      id: value.id,
      grupoId: value.grupo.id,
      fechaInicio: LuxonUtils.toBackend(value.fechaInicio),
      fechaFin: LuxonUtils.toBackend(value.fechaFin),
      especialInvestigacion: value.especialInvestigacion
    };
  }
}

export const GRUPO_ESPECIAL_INVESTIGACION_RESPONSE_CONVERTER = new GrupoEspecialInvestigacionResponseConverter();
