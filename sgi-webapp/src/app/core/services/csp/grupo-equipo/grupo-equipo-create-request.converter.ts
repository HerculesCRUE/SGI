import { IGrupo } from '@core/models/csp/grupo';
import { IGrupoEquipo } from '@core/models/csp/grupo-equipo';
import { IRolProyecto } from '@core/models/csp/rol-proyecto';
import { IPersona } from '@core/models/sgp/persona';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { SgiBaseConverter } from '@sgi/framework/core';
import { IGrupoEquipoCreateRequest } from './grupo-equipo-create-request';

class GrupoEquipCreateRequestConverter
  extends SgiBaseConverter<IGrupoEquipoCreateRequest, IGrupoEquipo> {
  toTarget(value: IGrupoEquipoCreateRequest): IGrupoEquipo {
    if (!value) {
      return value as unknown as IGrupoEquipo;
    }
    return {
      id: undefined,
      persona: value.personaRef ? { id: value.personaRef } as IPersona : undefined,
      grupo: value.grupoId ? { id: value.grupoId } as IGrupo : undefined,
      fechaInicio: LuxonUtils.fromBackend(value.fechaInicio),
      fechaFin: undefined,
      rol: value.rolId ? { id: value.rolId } as IRolProyecto : undefined,
      dedicacion: null,
      participacion: null
    };
  }

  fromTarget(value: IGrupoEquipo): IGrupoEquipoCreateRequest {
    if (!value) {
      return value as unknown as IGrupoEquipoCreateRequest;
    }
    return {
      personaRef: value.persona.id,
      grupoId: value.grupo.id,
      fechaInicio: LuxonUtils.toBackend(value.fechaInicio),
      rolId: value.rol.id,
    };
  }
}

export const GRUPO_EQUIPO_CREATE_REQUEST_CONVERTER = new GrupoEquipCreateRequestConverter();
