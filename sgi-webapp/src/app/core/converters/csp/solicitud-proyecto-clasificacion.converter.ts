import { ISolicitudProyectoClasificacionBackend } from '@core/models/csp/backend/solicitud-proyecto-clasificacion-backend';
import { ISolicitudProyectoClasificacion } from '@core/models/csp/solicitud-proyecto-clasificacion';
import { IClasificacion } from '@core/models/sgo/clasificacion';
import { SgiBaseConverter } from '@sgi/framework/core';

class SolicitudProyectoClasificacionConverter
  extends SgiBaseConverter<ISolicitudProyectoClasificacionBackend, ISolicitudProyectoClasificacion> {

  toTarget(value: ISolicitudProyectoClasificacionBackend): ISolicitudProyectoClasificacion {
    if (!value) {
      return value as unknown as ISolicitudProyectoClasificacion;
    }
    return {
      id: value.id,
      clasificacion: { id: value.clasificacionRef } as IClasificacion,
      solicitudProyectoId: value.solicitudProyectoId,
    };
  }

  fromTarget(value: ISolicitudProyectoClasificacion): ISolicitudProyectoClasificacionBackend {
    if (!value) {
      return value as unknown as ISolicitudProyectoClasificacionBackend;
    }
    return {
      id: value.id,
      clasificacionRef: value.clasificacion.id,
      solicitudProyectoId: value.solicitudProyectoId
    };
  }
}

export const SOLICITUD_PROYECTO_CLASIFICACION_CONVERTER = new SolicitudProyectoClasificacionConverter();
