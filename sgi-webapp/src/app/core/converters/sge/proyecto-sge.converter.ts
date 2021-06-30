import { IProyectoSgeBackend } from '@core/models/sge/backend/proyecto-sge-backend';
import { IProyectoSge } from '@core/models/sge/proyecto-sge';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { SgiBaseConverter } from '@sgi/framework/core';

class ProyectoSgeConverter extends SgiBaseConverter<IProyectoSgeBackend, IProyectoSge> {
  toTarget(value: IProyectoSgeBackend): IProyectoSge {
    if (!value) {
      return value as unknown as IProyectoSge;
    }
    return {
      id: value.id,
      titulo: value.titulo,
      fechaInicio: LuxonUtils.fromBackend(value.fechaInicio),
      fechaFin: LuxonUtils.fromBackend(value.fechaFin),
      proyectoSGIId: value.proyectoSGIId,
      sectorIva: value.sectorIva
    };
  }

  fromTarget(value: IProyectoSge): IProyectoSgeBackend {
    if (!value) {
      return value as unknown as IProyectoSgeBackend;
    }
    return {
      id: value.id,
      titulo: value.titulo,
      fechaInicio: LuxonUtils.toBackend(value.fechaInicio),
      fechaFin: LuxonUtils.toBackend(value.fechaFin),
      proyectoSGIId: value.proyectoSGIId,
      sectorIva: value.sectorIva
    };
  }
}

export const PROYECTO_SGE_CONVERTER = new ProyectoSgeConverter();
