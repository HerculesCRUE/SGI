import { ISolicitudProyectoResponsableEconomico } from '@core/models/csp/solicitud-proyecto-responsable-economico';
import { IPersona } from '@core/models/sgp/persona';
import { SgiBaseConverter } from '@sgi/framework/core';
import { ISolicitudProyectoResponsableEconomicoResponse } from './solicitud-proyecto-responsable-economico-response';

class SolicitudProyectoResponsableEconomicoResponseConverter
  extends SgiBaseConverter<ISolicitudProyectoResponsableEconomicoResponse, ISolicitudProyectoResponsableEconomico>{
  toTarget(value: ISolicitudProyectoResponsableEconomicoResponse): ISolicitudProyectoResponsableEconomico {
    if (!value) {
      return value as unknown as ISolicitudProyectoResponsableEconomico;
    }
    return {
      id: value.id,
      solicitudProyectoId: value.solicitudProyectoId,
      persona: { id: value.personaRef } as IPersona,
      mesInicio: value.mesInicio,
      mesFin: value.mesFin
    };
  }
  fromTarget(value: ISolicitudProyectoResponsableEconomico): ISolicitudProyectoResponsableEconomicoResponse {
    if (!value) {
      return value as unknown as ISolicitudProyectoResponsableEconomicoResponse;
    }
    return {
      id: value.id,
      solicitudProyectoId: value.solicitudProyectoId,
      personaRef: value.persona?.id,
      mesInicio: value.mesInicio,
      mesFin: value.mesFin
    };
  }
}

export const SOLICITUD_PROYECTO_RESPONSABLE_ECONOMICO_RESPONSE_CONVERTER = new SolicitudProyectoResponsableEconomicoResponseConverter();
