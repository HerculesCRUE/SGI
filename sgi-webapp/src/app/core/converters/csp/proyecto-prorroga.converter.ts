import { IProyectoProrrogaBackend } from '@core/models/csp/backend/proyecto-prorroga-backend';
import { IProyectoProrroga } from '@core/models/csp/proyecto-prorroga';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { SgiBaseConverter } from '@sgi/framework/core';

class ProyectoProrrogaConverter extends SgiBaseConverter<IProyectoProrrogaBackend, IProyectoProrroga> {

  toTarget(value: IProyectoProrrogaBackend): IProyectoProrroga {
    if (!value) {
      return value as unknown as IProyectoProrroga;
    }
    return {
      id: value.id,
      proyectoId: value.proyectoId,
      numProrroga: value.numProrroga,
      fechaConcesion: LuxonUtils.fromBackend(value.fechaConcesion),
      tipo: value.tipo,
      fechaFin: LuxonUtils.fromBackend(value.fechaFin),
      importe: value.importe,
      observaciones: value.observaciones
    };
  }

  fromTarget(value: IProyectoProrroga): IProyectoProrrogaBackend {
    if (!value) {
      return value as unknown as IProyectoProrrogaBackend;
    }
    return {
      id: value.id,
      proyectoId: value.proyectoId,
      numProrroga: value.numProrroga,
      fechaConcesion: LuxonUtils.toBackend(value.fechaConcesion),
      tipo: value.tipo,
      fechaFin: LuxonUtils.toBackend(value.fechaFin),
      importe: value.importe,
      observaciones: value.observaciones
    };
  }
}

export const PROYECTO_PRORROGA_CONVERTER = new ProyectoProrrogaConverter();
