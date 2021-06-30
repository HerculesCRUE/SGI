import { IProyectoClasificacionBackend } from '@core/models/csp/backend/proyecto-clasificacion-backend';
import { IProyectoClasificacion } from '@core/models/csp/proyecto-clasificacion';
import { IClasificacion } from '@core/models/sgo/clasificacion';
import { SgiBaseConverter } from '@sgi/framework/core';

class ProyectoClasificacionConverter
  extends SgiBaseConverter<IProyectoClasificacionBackend, IProyectoClasificacion> {

  toTarget(value: IProyectoClasificacionBackend): IProyectoClasificacion {
    if (!value) {
      return value as unknown as IProyectoClasificacion;
    }
    return {
      id: value.id,
      clasificacion: { id: value.clasificacionRef } as IClasificacion,
      proyectoId: value.proyectoId,
    };
  }

  fromTarget(value: IProyectoClasificacion): IProyectoClasificacionBackend {
    if (!value) {
      return value as unknown as IProyectoClasificacionBackend;
    }
    return {
      id: value.id,
      clasificacionRef: value.clasificacion.id,
      proyectoId: value.proyectoId
    };
  }
}

export const PROYECTO_CLASIFICACION_CONVERTER = new ProyectoClasificacionConverter();
