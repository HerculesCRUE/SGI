import { IProyectoPaqueteTrabajoBackend } from '@core/models/csp/backend/proyecto-paquete-trabajo-backend';
import { IProyectoPaqueteTrabajo } from '@core/models/csp/proyecto-paquete-trabajo';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { SgiBaseConverter } from '@sgi/framework/core';

class ProyectoPaqueteTrabajoConverter extends SgiBaseConverter<IProyectoPaqueteTrabajoBackend, IProyectoPaqueteTrabajo> {

  toTarget(value: IProyectoPaqueteTrabajoBackend): IProyectoPaqueteTrabajo {
    if (!value) {
      return value as unknown as IProyectoPaqueteTrabajo;
    }
    return {
      id: value.id,
      proyectoId: value.proyectoId,
      nombre: value.nombre,
      fechaInicio: LuxonUtils.fromBackend(value.fechaInicio),
      fechaFin: LuxonUtils.fromBackend(value.fechaFin),
      personaMes: value.personaMes,
      descripcion: value.descripcion
    };
  }

  fromTarget(value: IProyectoPaqueteTrabajo): IProyectoPaqueteTrabajoBackend {
    if (!value) {
      return value as unknown as IProyectoPaqueteTrabajoBackend;
    }
    return {
      id: value.id,
      proyectoId: value.proyectoId,
      nombre: value.nombre,
      fechaInicio: LuxonUtils.toBackend(value.fechaInicio),
      fechaFin: LuxonUtils.toBackend(value.fechaFin),
      personaMes: value.personaMes,
      descripcion: value.descripcion
    };
  }
}

export const PROYECTO_PAQUETE_TRABAJO_CONVERTER = new ProyectoPaqueteTrabajoConverter();
