import { IGrupo } from '@core/models/csp/grupo';
import { IGrupoEquipo } from '@core/models/csp/grupo-equipo';
import { IRolProyecto } from '@core/models/csp/rol-proyecto';
import { IPersona } from '@core/models/sgp/persona';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { SgiBaseConverter } from '@sgi/framework/core';
import { IGrupoEquipoResponse } from './grupo-equipo-response';

class GrupoEquipoResponseConverter
  extends SgiBaseConverter<IGrupoEquipoResponse, IGrupoEquipo> {
  toTarget(value: IGrupoEquipoResponse): IGrupoEquipo {
    if (!value) {
      return value as unknown as IGrupoEquipo;
    }
    return {
      id: value.id,
      persona: value.personaRef ? { id: value.personaRef } as IPersona : undefined,
      grupo: value.grupoId ? { id: value.grupoId } as IGrupo : undefined,
      fechaInicio: LuxonUtils.fromBackend(value.fechaInicio),
      fechaFin: LuxonUtils.fromBackend(value.fechaFin),
      rol: value.rol as IRolProyecto,
      dedicacion: value.dedicacion,
      participacion: value.participacion
    };
  }

  fromTarget(value: IGrupoEquipo): IGrupoEquipoResponse {
    if (!value) {
      return value as unknown as IGrupoEquipoResponse;
    }
    return {
      id: value.id,
      personaRef: value.persona.id,
      grupoId: value.grupo.id,
      fechaInicio: LuxonUtils.toBackend(value.fechaInicio),
      fechaFin: LuxonUtils.toBackend(value.fechaFin),
      rol: value.rol,
      dedicacion: value.dedicacion,
      participacion: value.participacion
    };
  }
}

export const GRUPO_EQUIPO_RESPONSE_CONVERTER = new GrupoEquipoResponseConverter();
