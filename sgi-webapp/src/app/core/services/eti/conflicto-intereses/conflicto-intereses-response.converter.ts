import { IConflictoInteres } from '@core/models/eti/conflicto-interes';
import { IPersona } from '@core/models/sgp/persona';
import { IConflictoInteresResponse } from '@core/services/eti/conflicto-intereses/conflicto-intereses-response';
import { SgiBaseConverter } from '@sgi/framework/core';
import { EVALUADOR_RESPONSE_CONVERTER } from '../evaluador/evaluador-response.converter';

class ConflictoInteresesResponseConverter extends SgiBaseConverter<IConflictoInteresResponse, IConflictoInteres> {
  toTarget(value: IConflictoInteresResponse): IConflictoInteres {
    if (!value) {
      return value as unknown as IConflictoInteres;
    }
    return {
      id: value.id,
      evaluador: EVALUADOR_RESPONSE_CONVERTER.toTarget(value.evaluador),
      personaConflicto: { id: value.personaConflictoRef } as IPersona
    };
  }

  fromTarget(value: IConflictoInteres): IConflictoInteresResponse {
    if (!value) {
      return value as unknown as IConflictoInteresResponse;
    }
    return {
      id: value.id,
      evaluador: EVALUADOR_RESPONSE_CONVERTER.fromTarget(value.evaluador),
      personaConflictoRef: value.personaConflicto?.id
    };
  }
}

export const CONFLICTO_INTERESES_RESPONSE_CONVERTER = new ConflictoInteresesResponseConverter();
