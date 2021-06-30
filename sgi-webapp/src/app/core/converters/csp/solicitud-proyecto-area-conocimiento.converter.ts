import { ISolicitudProyectoAreaConocimiento } from '@core/models/csp/solicitud-proyecto-area-conocimiento';
import { IAreaConocimiento } from '@core/models/sgo/area-conocimiento';
import { SgiBaseConverter } from '@sgi/framework/core';
import { ISolicitudProyectoAreaConocimientoBackend } from 'src/app/core/models/csp/backend/solicitud-proyecto-area-conocimiento-backend';

class SolicitudProyectoAreaConocimientoConverter extends
  SgiBaseConverter<ISolicitudProyectoAreaConocimientoBackend, ISolicitudProyectoAreaConocimiento> {

  toTarget(value: ISolicitudProyectoAreaConocimientoBackend): ISolicitudProyectoAreaConocimiento {
    if (!value) {
      return value as unknown as ISolicitudProyectoAreaConocimiento;
    }
    return {
      id: value.id,
      solicitudProyectoId: value.solicitudProyectoId,
      areaConocimiento: { id: value.areaConocimientoRef } as IAreaConocimiento,
    };
  }

  fromTarget(value: ISolicitudProyectoAreaConocimiento): ISolicitudProyectoAreaConocimientoBackend {
    if (!value) {
      return value as unknown as ISolicitudProyectoAreaConocimientoBackend;
    }
    return {
      id: value.id,
      solicitudProyectoId: value.solicitudProyectoId,
      areaConocimientoRef: value.areaConocimiento.id,
    };
  }
}

export const SOLICITUD_PROYECTO_AREA_CONOCIMIENTO_CONVERTER = new SolicitudProyectoAreaConocimientoConverter();
