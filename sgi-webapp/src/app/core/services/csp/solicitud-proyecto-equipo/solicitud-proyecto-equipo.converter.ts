import { ISolicitudProyectoEquipo } from '@core/models/csp/solicitud-proyecto-equipo';
import { IPersona } from '@core/models/sgp/persona';
import { ROL_PROYECTO_RESPONSE_CONVERTER } from '@core/services/csp/rol-proyecto/rol-proyecto-response.converter';
import { SgiBaseConverter } from '@sgi/framework/core';
import { ISolicitudProyectoEquipoResponse } from './solicitud-proyecto-equipo-response';

class SolicitudProyectoEquipoConverter extends SgiBaseConverter<ISolicitudProyectoEquipoResponse, ISolicitudProyectoEquipo> {

  toTarget(value: ISolicitudProyectoEquipoResponse): ISolicitudProyectoEquipo {
    if (!value) {
      return value as unknown as ISolicitudProyectoEquipo;
    }
    return {
      id: value.id,
      mesFin: value.mesFin,
      mesInicio: value.mesInicio,
      persona: { id: value.personaRef } as IPersona,
      rolProyecto: ROL_PROYECTO_RESPONSE_CONVERTER.toTarget(value.rolProyecto),
      solicitudProyectoId: value.solicitudProyectoId
    };
  }

  fromTarget(value: ISolicitudProyectoEquipo): ISolicitudProyectoEquipoResponse {
    if (!value) {
      return value as unknown as ISolicitudProyectoEquipoResponse;
    }
    return {
      id: value.id,
      mesFin: value.mesFin,
      mesInicio: value.mesInicio,
      personaRef: value.persona.id,
      rolProyecto: ROL_PROYECTO_RESPONSE_CONVERTER.fromTarget(value.rolProyecto),
      solicitudProyectoId: value.solicitudProyectoId
    };
  }
}

export const SOLICITUD_PROYECTO_EQUIPO_RESPONSE_CONVERTER = new SolicitudProyectoEquipoConverter();
