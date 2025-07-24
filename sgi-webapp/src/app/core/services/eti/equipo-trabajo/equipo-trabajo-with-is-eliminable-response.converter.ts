import { IEquipoTrabajoWithIsEliminable } from '@core/models/eti/equipo-trabajo-with-is-eliminable';
import { IPersona } from '@core/models/sgp/persona';
import { SgiBaseConverter } from '@sgi/framework/core';
import { IEquipoTrabajoWithIsEliminableResponse } from './equipo-trabajo-with-is-eliminable-response';

class EquipoTrabajoWithIsEliminableResponseConverter
  extends SgiBaseConverter<IEquipoTrabajoWithIsEliminableResponse, IEquipoTrabajoWithIsEliminable> {
  toTarget(value: IEquipoTrabajoWithIsEliminableResponse): IEquipoTrabajoWithIsEliminable {
    if (!value) {
      return value as unknown as IEquipoTrabajoWithIsEliminable;
    }
    return {
      id: value.id,
      persona: { id: value.personaRef } as IPersona,
      peticionEvaluacionId: value.peticionEvaluacionId,
      eliminable: value.eliminable
    };
  }

  fromTarget(value: IEquipoTrabajoWithIsEliminable): IEquipoTrabajoWithIsEliminableResponse {
    if (!value) {
      return value as unknown as IEquipoTrabajoWithIsEliminableResponse;
    }
    return {
      id: value.id,
      personaRef: value.persona?.id,
      peticionEvaluacionId: value.peticionEvaluacionId,
      eliminable: value.eliminable
    };
  }
}

export const EQUIPO_TRABAJO_WITH_IS_ELIMINABLE_RESPONSE_CONVERTER = new EquipoTrabajoWithIsEliminableResponseConverter();
