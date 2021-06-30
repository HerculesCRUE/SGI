import { ISolicitudProyectoSocioEquipoBackend } from '@core/models/csp/backend/solicitud-proyecto-socio-equipo-backend';
import { ISolicitudProyectoSocioEquipo } from '@core/models/csp/solicitud-proyecto-socio-equipo';
import { IPersona } from '@core/models/sgp/persona';
import { SgiBaseConverter } from '@sgi/framework/core';

class SolicitudProyectoSocioEquipoConverter extends SgiBaseConverter<ISolicitudProyectoSocioEquipoBackend, ISolicitudProyectoSocioEquipo> {

  toTarget(value: ISolicitudProyectoSocioEquipoBackend): ISolicitudProyectoSocioEquipo {
    if (!value) {
      return value as unknown as ISolicitudProyectoSocioEquipo;
    }
    return {
      id: value.id,
      mesFin: value.mesFin,
      mesInicio: value.mesInicio,
      persona: { id: value.personaRef } as IPersona,
      rolProyecto: value.rolProyecto,
      solicitudProyectoSocioId: value.solicitudProyectoSocioId
    };
  }

  fromTarget(value: ISolicitudProyectoSocioEquipo): ISolicitudProyectoSocioEquipoBackend {
    if (!value) {
      return value as unknown as ISolicitudProyectoSocioEquipoBackend;
    }
    return {
      id: value.id,
      mesFin: value.mesFin,
      mesInicio: value.mesInicio,
      personaRef: value.persona.id,
      rolProyecto: value.rolProyecto,
      solicitudProyectoSocioId: value.solicitudProyectoSocioId
    };
  }
}

export const SOLICITUD_PROYECTO_SOCIO_EQUIPO_CONVERTER = new SolicitudProyectoSocioEquipoConverter();
