import { IProyectoAreaConocimientoBackend } from '@core/models/csp/backend/proyecto-area-conocimiento-backend';
import { IProyectoAreaConocimiento } from '@core/models/csp/proyecto-area-conocimiento';
import { IAreaConocimiento } from '@core/models/sgo/area-conocimiento';
import { SgiBaseConverter } from '@sgi/framework/core';

class ProyectoAreaConocimientoConverter
  extends SgiBaseConverter<IProyectoAreaConocimientoBackend, IProyectoAreaConocimiento> {

  toTarget(value: IProyectoAreaConocimientoBackend): IProyectoAreaConocimiento {
    if (!value) {
      return value as unknown as IProyectoAreaConocimiento;
    }
    return {
      id: value.id,
      proyectoId: value.proyectoId,
      areaConocimiento: { id: value.areaConocimientoRef } as IAreaConocimiento,
    };
  }

  fromTarget(value: IProyectoAreaConocimiento): IProyectoAreaConocimientoBackend {
    if (!value) {
      return value as unknown as IProyectoAreaConocimientoBackend;
    }
    return {
      id: value.id,
      proyectoId: value.proyectoId,
      areaConocimientoRef: value.areaConocimiento.id,
    };
  }
}

export const PROYECTO_AREA_CONOCIMIENTO_CONVERTER = new ProyectoAreaConocimientoConverter();
