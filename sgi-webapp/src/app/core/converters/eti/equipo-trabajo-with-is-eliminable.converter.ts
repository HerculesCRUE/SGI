import { IEquipoTrabajoWithIsEliminableBackend } from '@core/models/eti/backend/equipo-trabajo-with-is-eliminable-backend';
import { IEquipoTrabajoWithIsEliminable } from '@core/models/eti/equipo-trabajo-with-is-eliminable';
import { IPersona } from '@core/models/sgp/persona';
import { SgiBaseConverter } from '@sgi/framework/core';
import { PETICION_EVALUACION_CONVERTER } from './peticion-evaluacion.converter';

class EquipoTrabajoWithIsEliminableConverter
  extends SgiBaseConverter<IEquipoTrabajoWithIsEliminableBackend, IEquipoTrabajoWithIsEliminable> {
  toTarget(value: IEquipoTrabajoWithIsEliminableBackend): IEquipoTrabajoWithIsEliminable {
    if (!value) {
      return value as unknown as IEquipoTrabajoWithIsEliminable;
    }
    return {
      id: value.id,
      persona: { id: value.personaRef } as IPersona,
      peticionEvaluacion: PETICION_EVALUACION_CONVERTER.toTarget(value.peticionEvaluacion),
      eliminable: value.eliminable
    };
  }

  fromTarget(value: IEquipoTrabajoWithIsEliminable): IEquipoTrabajoWithIsEliminableBackend {
    if (!value) {
      return value as unknown as IEquipoTrabajoWithIsEliminableBackend;
    }
    return {
      id: value.id,
      personaRef: value.persona?.id,
      peticionEvaluacion: PETICION_EVALUACION_CONVERTER.fromTarget(value.peticionEvaluacion),
      eliminable: value.eliminable
    };
  }
}

export const EQUIPO_TRABAJO_WITH_IS_ELIMINABLE_CONVERTER = new EquipoTrabajoWithIsEliminableConverter();
