import { IEquipoTrabajoBackend } from '@core/models/eti/backend/equipo-trabajo-backend';
import { IEquipoTrabajo } from '@core/models/eti/equipo-trabajo';
import { IPersona } from '@core/models/sgp/persona';
import { SgiBaseConverter } from '@sgi/framework/core';
import { PETICION_EVALUACION_CONVERTER } from './peticion-evaluacion.converter';

class EquipoTrabajoConverter extends SgiBaseConverter<IEquipoTrabajoBackend, IEquipoTrabajo> {
  toTarget(value: IEquipoTrabajoBackend): IEquipoTrabajo {
    if (!value) {
      return value as unknown as IEquipoTrabajo;
    }
    return {
      id: value.id,
      persona: { id: value.personaRef } as IPersona,
      peticionEvaluacion: PETICION_EVALUACION_CONVERTER.toTarget(value.peticionEvaluacion)
    };
  }

  fromTarget(value: IEquipoTrabajo): IEquipoTrabajoBackend {
    if (!value) {
      return value as unknown as IEquipoTrabajoBackend;
    }
    return {
      id: value.id,
      personaRef: value.persona?.id,
      peticionEvaluacion: PETICION_EVALUACION_CONVERTER.fromTarget(value.peticionEvaluacion)
    };
  }
}

export const EQUIPO_TRABAJO_CONVERTER = new EquipoTrabajoConverter();
