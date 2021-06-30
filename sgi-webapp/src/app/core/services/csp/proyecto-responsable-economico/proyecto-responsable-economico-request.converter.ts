import { IProyectoResponsableEconomico } from '@core/models/csp/proyecto-responsable-economico';
import { IPersona } from '@core/models/sgp/persona';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { SgiBaseConverter } from '@sgi/framework/core';
import { IProyectoResponsableEconomicoRequest } from './proyecto-responsable-economico-request';

class ProyectoResponsableEconomicoRequestConverter
  extends SgiBaseConverter<IProyectoResponsableEconomicoRequest, IProyectoResponsableEconomico>{
  toTarget(value: IProyectoResponsableEconomicoRequest): IProyectoResponsableEconomico {
    if (!value) {
      return value as unknown as IProyectoResponsableEconomico;
    }
    return {
      id: value.id,
      proyectoId: undefined,
      persona: { id: value.personaRef } as IPersona,
      fechaInicio: LuxonUtils.fromBackend(value.fechaInicio),
      fechaFin: LuxonUtils.fromBackend(value.fechaFin)
    };
  }
  fromTarget(value: IProyectoResponsableEconomico): IProyectoResponsableEconomicoRequest {
    if (!value) {
      return value as unknown as IProyectoResponsableEconomicoRequest;
    }
    return {
      id: value.id,
      personaRef: value.persona?.id,
      fechaInicio: LuxonUtils.toBackend(value.fechaInicio),
      fechaFin: LuxonUtils.toBackend(value.fechaFin)
    };
  }
}

export const PROYECTO_RESPONSABLE_ECONOMICO_REQUEST_CONVERTER = new ProyectoResponsableEconomicoRequestConverter();
