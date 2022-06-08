import { IProyectosCompetitivosPersonas } from '@core/models/csp/proyectos-competitivos-personas';
import { SgiBaseConverter } from '@sgi/framework/core';
import { IProyectosCompetitivosPersonasResponse } from './proyectos-competitivos-personas-response';

class ProyectosCompetitivosPersonasResponseConverter
  extends SgiBaseConverter<IProyectosCompetitivosPersonasResponse, IProyectosCompetitivosPersonas> {
  toTarget(value: IProyectosCompetitivosPersonasResponse): IProyectosCompetitivosPersonas {
    if (!value) {
      return value as unknown as IProyectosCompetitivosPersonas;
    }
    return {
      numProyectosCompetitivos: value.numProyectosCompetitivos,
      numProyectosCompetitivosActuales: value.numProyectosCompetitivosActuales,
      numProyectosNoCompetitivos: value.numProyectosNoCompetitivos,
      numProyectosNoCompetitivosActuales: value.numProyectosNoCompetitivosActuales
    };
  }

  fromTarget(value: IProyectosCompetitivosPersonas): IProyectosCompetitivosPersonasResponse {
    if (!value) {
      return value as unknown as IProyectosCompetitivosPersonasResponse;
    }
    return {
      numProyectosCompetitivos: value.numProyectosCompetitivos,
      numProyectosCompetitivosActuales: value.numProyectosCompetitivosActuales,
      numProyectosNoCompetitivos: value.numProyectosNoCompetitivos,
      numProyectosNoCompetitivosActuales: value.numProyectosNoCompetitivosActuales
    };
  }
}

export const PROYECTOS_COMPETITIVOS_PERSONAS_RESPONSE_CONVERTER = new ProyectosCompetitivosPersonasResponseConverter();
