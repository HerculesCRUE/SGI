import { IProyectoPlazoBackend } from '@core/models/csp/backend/proyecto-plazo-backend';
import { IProyectoPlazos } from '@core/models/csp/proyecto-plazo';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { SgiBaseConverter } from '@sgi/framework/core';

class ProyectoPlazoConverter extends SgiBaseConverter<IProyectoPlazoBackend, IProyectoPlazos> {

  toTarget(value: IProyectoPlazoBackend): IProyectoPlazos {
    if (!value) {
      return value as unknown as IProyectoPlazos;
    }
    return {
      id: value.id,
      proyectoId: value.proyectoId,
      tipoFase: value.tipoFase,
      fechaInicio: LuxonUtils.fromBackend(value.fechaInicio),
      fechaFin: LuxonUtils.fromBackend(value.fechaFin),
      observaciones: value.observaciones,
      generaAviso: value.generaAviso
    };
  }

  fromTarget(value: IProyectoPlazos): IProyectoPlazoBackend {
    if (!value) {
      return value as unknown as IProyectoPlazoBackend;
    }
    return {
      id: value.id,
      proyectoId: value.proyectoId,
      tipoFase: value.tipoFase,
      fechaInicio: LuxonUtils.toBackend(value.fechaInicio),
      fechaFin: LuxonUtils.toBackend(value.fechaFin),
      observaciones: value.observaciones,
      generaAviso: value.generaAviso
    };
  }
}

export const PROYECTO_PLAZO_CONVERTER = new ProyectoPlazoConverter();
