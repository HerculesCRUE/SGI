import { IProyectoEquipo } from '@core/models/csp/proyecto-equipo';
import { IPersona } from '@core/models/sgp/persona';
import { ROL_PROYECTO_RESPONSE_CONVERTER } from '@core/services/csp/rol-proyecto/rol-proyecto-response.converter';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { SgiBaseConverter } from '@sgi/framework/core';
import { IProyectoEquipoResponse } from './proyecto-equipo-response';

class ProyectoEquipoConverter extends SgiBaseConverter<IProyectoEquipoResponse, IProyectoEquipo> {

  toTarget(value: IProyectoEquipoResponse): IProyectoEquipo {
    if (!value) {
      return value as unknown as IProyectoEquipo;
    }
    return {
      id: value.id,
      fechaFin: LuxonUtils.fromBackend(value.fechaFin),
      fechaInicio: LuxonUtils.fromBackend(value.fechaInicio),
      persona: { id: value.personaRef } as IPersona,
      rolProyecto: ROL_PROYECTO_RESPONSE_CONVERTER.toTarget(value.rolProyecto),
      proyectoId: value.proyectoId,
    };
  }

  fromTarget(value: IProyectoEquipo): IProyectoEquipoResponse {
    if (!value) {
      return value as unknown as IProyectoEquipoResponse;
    }
    return {
      id: value.id,
      fechaFin: LuxonUtils.toBackend(value.fechaFin),
      fechaInicio: LuxonUtils.toBackend(value.fechaInicio),
      personaRef: value.persona.id,
      rolProyecto: ROL_PROYECTO_RESPONSE_CONVERTER.fromTarget(value.rolProyecto),
      proyectoId: value.proyectoId,
    };
  }
}

export const PROYECTO_EQUIPO_RESPONSE_CONVERTER = new ProyectoEquipoConverter();
