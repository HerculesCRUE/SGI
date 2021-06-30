import { ISolicitudProyectoResponsableEconomico } from '@core/models/csp/solicitud-proyecto-responsable-economico';
import { IPersona } from '@core/models/sgp/persona';
import { SgiBaseConverter } from '@sgi/framework/core';
import { ISolicitudProyectoResponsableEconomicoRequest } from './solicitud-proyecto-responsable-economico-request';

class SolicitudProyectoResponsableEconomicoRequestConverter
  extends SgiBaseConverter<ISolicitudProyectoResponsableEconomicoRequest, ISolicitudProyectoResponsableEconomico>{
  toTarget(value: ISolicitudProyectoResponsableEconomicoRequest): ISolicitudProyectoResponsableEconomico {
    if (!value) {
      return value as unknown as ISolicitudProyectoResponsableEconomico;
    }
    return {
      id: value.id,
      solicitudProyectoId: undefined,
      persona: { id: value.personaRef } as IPersona,
      mesInicio: value.mesInicio,
      mesFin: value.mesFin
    };
  }
  fromTarget(value: ISolicitudProyectoResponsableEconomico): ISolicitudProyectoResponsableEconomicoRequest {
    if (!value) {
      return value as unknown as ISolicitudProyectoResponsableEconomicoRequest;
    }
    return {
      id: value.id,
      personaRef: value.persona?.id,
      mesInicio: value.mesInicio,
      mesFin: value.mesFin
    };
  }
}

export const SOLICITUD_PROYECTO_RESPONSABLE_ECONOMICO_REQUEST_CONVERTER = new SolicitudProyectoResponsableEconomicoRequestConverter();
