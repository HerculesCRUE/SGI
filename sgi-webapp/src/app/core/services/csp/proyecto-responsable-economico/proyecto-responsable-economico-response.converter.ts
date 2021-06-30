import { IProyectoResponsableEconomico } from '@core/models/csp/proyecto-responsable-economico';
import { IPersona } from '@core/models/sgp/persona';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { SgiBaseConverter } from '@sgi/framework/core';
import { IProyectoResponsableEconomicoResponse } from './proyecto-responsable-economico-response';

class ProyectoResponsableEconomicoResponseConverter
  extends SgiBaseConverter<IProyectoResponsableEconomicoResponse, IProyectoResponsableEconomico>{
  toTarget(value: IProyectoResponsableEconomicoResponse): IProyectoResponsableEconomico {
    if (!value) {
      return value as unknown as IProyectoResponsableEconomico;
    }
    return {
      id: value.id,
      proyectoId: value.proyectoId,
      persona: { id: value.personaRef } as IPersona,
      fechaInicio: LuxonUtils.fromBackend(value.fechaInicio),
      fechaFin: LuxonUtils.fromBackend(value.fechaFin)
    };
  }
  fromTarget(value: IProyectoResponsableEconomico): IProyectoResponsableEconomicoResponse {
    if (!value) {
      return value as unknown as IProyectoResponsableEconomicoResponse;
    }
    return {
      id: value.id,
      proyectoId: value.proyectoId,
      personaRef: value.persona?.id,
      fechaInicio: LuxonUtils.toBackend(value.fechaInicio),
      fechaFin: LuxonUtils.toBackend(value.fechaFin)
    };
  }
}

export const PROYECTO_RESPONSABLE_ECONOMICO_RESPONSE_CONVERTER = new ProyectoResponsableEconomicoResponseConverter();
