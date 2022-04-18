import { IProyectoEquipoBackend } from '@core/models/csp/backend/proyecto-equipo-backend';
import { IProyectoEquipo } from '@core/models/csp/proyecto-equipo';
import { IPersona } from '@core/models/sgp/persona';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { SgiBaseConverter } from '@sgi/framework/core';

class ProyectoEquipoConverter extends SgiBaseConverter<IProyectoEquipoBackend, IProyectoEquipo> {

  toTarget(value: IProyectoEquipoBackend): IProyectoEquipo {
    if (!value) {
      return value as unknown as IProyectoEquipo;
    }
    return {
      id: value.id,
      fechaFin: LuxonUtils.fromBackend(value.fechaFin),
      fechaInicio: LuxonUtils.fromBackend(value.fechaInicio),
      persona: { id: value.personaRef } as IPersona,
      rolProyecto: value.rolProyecto,
      proyectoId: value.proyectoId,
    };
  }

  fromTarget(value: IProyectoEquipo): IProyectoEquipoBackend {
    if (!value) {
      return value as unknown as IProyectoEquipoBackend;
    }
    return {
      id: value.id,
      fechaFin: LuxonUtils.toBackend(value.fechaFin),
      fechaInicio: LuxonUtils.toBackend(value.fechaInicio),
      personaRef: value.persona.id,
      rolProyecto: value.rolProyecto,
      proyectoId: value.proyectoId,
    };
  }
}

export const PROYECTO_EQUIPO_CONVERTER = new ProyectoEquipoConverter();
