import { ISolicitudProyectoSocioEquipo } from '@core/models/csp/solicitud-proyecto-socio-equipo';
import { IPersona } from '@core/models/sgp/persona';
import { ROL_PROYECTO_RESPONSE_CONVERTER } from '@core/services/csp/rol-proyecto/rol-proyecto-response.converter';
import { SgiBaseConverter } from '@sgi/framework/core';
import { ISolicitudProyectoSocioEquipoResponse } from './solicitud-proyecto-socio-equipo-response';

class SolicitudProyectoSocioEquipoConverter extends SgiBaseConverter<ISolicitudProyectoSocioEquipoResponse, ISolicitudProyectoSocioEquipo> {

  toTarget(value: ISolicitudProyectoSocioEquipoResponse): ISolicitudProyectoSocioEquipo {
    if (!value) {
      return value as unknown as ISolicitudProyectoSocioEquipo;
    }
    return {
      id: value.id,
      mesFin: value.mesFin,
      mesInicio: value.mesInicio,
      persona: { id: value.personaRef } as IPersona,
      rolProyecto: ROL_PROYECTO_RESPONSE_CONVERTER.toTarget(value.rolProyecto),
      solicitudProyectoSocioId: value.solicitudProyectoSocioId
    };
  }

  fromTarget(value: ISolicitudProyectoSocioEquipo): ISolicitudProyectoSocioEquipoResponse {
    if (!value) {
      return value as unknown as ISolicitudProyectoSocioEquipoResponse;
    }
    return {
      id: value.id,
      mesFin: value.mesFin,
      mesInicio: value.mesInicio,
      personaRef: value.persona.id,
      rolProyecto: ROL_PROYECTO_RESPONSE_CONVERTER.fromTarget(value.rolProyecto),
      solicitudProyectoSocioId: value.solicitudProyectoSocioId
    };
  }
}

export const SOLICITUD_PROYECTO_SOCIO_EQUIPO_RESPONSE_CONVERTER = new SolicitudProyectoSocioEquipoConverter();
