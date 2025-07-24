import { IProyectoSocioEquipo } from '@core/models/csp/proyecto-socio-equipo';
import { IPersona } from '@core/models/sgp/persona';
import { ROL_PROYECTO_RESPONSE_CONVERTER } from '@core/services/csp/rol-proyecto/rol-proyecto-response.converter';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { SgiBaseConverter } from '@sgi/framework/core';
import { IProyectoSocioEquipoResponse } from './proyecto-socio-equipo-response';

class ProyectoSocioEquipoConverter extends SgiBaseConverter<IProyectoSocioEquipoResponse, IProyectoSocioEquipo> {

  toTarget(value: IProyectoSocioEquipoResponse): IProyectoSocioEquipo {
    if (!value) {
      return value as unknown as IProyectoSocioEquipo;
    }
    return {
      id: value.id,
      fechaFin: LuxonUtils.fromBackend(value.fechaFin),
      fechaInicio: LuxonUtils.fromBackend(value.fechaInicio),
      persona: { id: value.personaRef } as IPersona,
      rolProyecto: ROL_PROYECTO_RESPONSE_CONVERTER.toTarget(value.rolProyecto),
      proyectoSocioId: value.proyectoSocioId
    };
  }

  fromTarget(value: IProyectoSocioEquipo): IProyectoSocioEquipoResponse {
    if (!value) {
      return value as unknown as IProyectoSocioEquipoResponse;
    }
    return {
      id: value.id,
      fechaFin: LuxonUtils.toBackend(value.fechaFin),
      fechaInicio: LuxonUtils.toBackend(value.fechaInicio),
      personaRef: value.persona?.id,
      rolProyecto: ROL_PROYECTO_RESPONSE_CONVERTER.fromTarget(value.rolProyecto),
      proyectoSocioId: value.proyectoSocioId
    };
  }
}

export const PROYECTO_SOCIO_EQUIPO_RESPONSE_CONVERTER = new ProyectoSocioEquipoConverter();
