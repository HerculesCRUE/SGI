import { ISolicitudProyectoEquipoBackend } from '@core/models/csp/backend/solicitud-proyecto-equipo-backend';
import { ISolicitudProyectoEquipo } from '@core/models/csp/solicitud-proyecto-equipo';
import { IPersona } from '@core/models/sgp/persona';
import { SgiBaseConverter } from '@sgi/framework/core';

class SolicitudProyectoEquipoConverter extends SgiBaseConverter<ISolicitudProyectoEquipoBackend, ISolicitudProyectoEquipo> {

  toTarget(value: ISolicitudProyectoEquipoBackend): ISolicitudProyectoEquipo {
    if (!value) {
      return value as unknown as ISolicitudProyectoEquipo;
    }
    return {
      id: value.id,
      mesFin: value.mesFin,
      mesInicio: value.mesInicio,
      persona: { id: value.personaRef } as IPersona,
      rolProyecto: value.rolProyecto,
      solicitudProyectoId: value.solicitudProyectoId
    };
  }

  fromTarget(value: ISolicitudProyectoEquipo): ISolicitudProyectoEquipoBackend {
    if (!value) {
      return value as unknown as ISolicitudProyectoEquipoBackend;
    }
    return {
      id: value.id,
      mesFin: value.mesFin,
      mesInicio: value.mesInicio,
      personaRef: value.persona.id,
      rolProyecto: value.rolProyecto,
      solicitudProyectoId: value.solicitudProyectoId
    };
  }
}

export const SOLICITUD_PROYECTO_EQUIPO_CONVERTER = new SolicitudProyectoEquipoConverter();
