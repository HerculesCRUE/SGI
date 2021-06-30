import { IConflictoInteresBackend } from '@core/models/eti/backend/conflicto-intereses-backend';
import { IConflictoInteres } from '@core/models/eti/conflicto-interes';
import { IPersona } from '@core/models/sgp/persona';
import { SgiBaseConverter } from '@sgi/framework/core';
import { EVALUADOR_CONVERTER } from './evaluador.converter';

class ConflictoInteresesConverter extends SgiBaseConverter<IConflictoInteresBackend, IConflictoInteres> {
  toTarget(value: IConflictoInteresBackend): IConflictoInteres {
    if (!value) {
      return value as unknown as IConflictoInteres;
    }
    return {
      id: value.id,
      evaluador: EVALUADOR_CONVERTER.toTarget(value.evaluador),
      personaConflicto: { id: value.personaConflictoRef } as IPersona
    };
  }

  fromTarget(value: IConflictoInteres): IConflictoInteresBackend {
    if (!value) {
      return value as unknown as IConflictoInteresBackend;
    }
    return {
      id: value.id,
      evaluador: EVALUADOR_CONVERTER.fromTarget(value.evaluador),
      personaConflictoRef: value.personaConflicto?.id
    };
  }
}

export const CONFLICTO_INTERESES_CONVERTER = new ConflictoInteresesConverter();
