import { IGrupo } from '@core/models/csp/grupo';
import { IGrupoEquipo } from '@core/models/csp/grupo-equipo';
import { IRolProyecto } from '@core/models/csp/rol-proyecto';
import { IPersona } from '@core/models/sgp/persona';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { SgiBaseConverter } from '@sgi/framework/core';
import { IGrupoEquipoUpdateRequest } from './grupo-equipo-update-request';

class GrupoEquipoUpdateRequestConverter
  extends SgiBaseConverter<IGrupoEquipoUpdateRequest, IGrupoEquipo> {
  toTarget(value: IGrupoEquipoUpdateRequest): IGrupoEquipo {
    if (!value) {
      return value as unknown as IGrupoEquipo;
    }
    return {
      id: undefined,
      persona: value.personaRef ? { id: value.personaRef } as IPersona : undefined,
      grupo: value.grupoId ? { id: value.grupoId } as IGrupo : undefined,
      fechaInicio: LuxonUtils.fromBackend(value.fechaInicio),
      fechaFin: LuxonUtils.fromBackend(value.fechaFin),
      rol: value.rolId ? { id: value.rolId } as IRolProyecto : undefined,
      dedicacion: value.dedicacion,
      participacion: value.participacion
    };
  }

  fromTarget(value: IGrupoEquipo): IGrupoEquipoUpdateRequest {
    if (!value) {
      return value as unknown as IGrupoEquipoUpdateRequest;
    }
    return {
      id: value.id,
      personaRef: value.persona.id,
      grupoId: value.grupo.id,
      fechaInicio: LuxonUtils.toBackend(value.fechaInicio),
      fechaFin: LuxonUtils.toBackend(value.fechaFin),
      rolId: value.rol.id,
      dedicacion: value.dedicacion,
      participacion: value.participacion
    };
  }
}

export const GRUPO_EQUIPO_UPDATE_REQUEST_CONVERTER = new GrupoEquipoUpdateRequestConverter();
