import { ISolicitudModalidadBackend } from '@core/models/csp/backend/solicitud-modalidad-backend';
import { ISolicitudModalidad } from '@core/models/csp/solicitud-modalidad';
import { IEmpresa } from '@core/models/sgemp/empresa';
import { SgiBaseConverter } from '@sgi/framework/core';

class SolicitudModalidadConverter extends SgiBaseConverter<ISolicitudModalidadBackend, ISolicitudModalidad> {

  toTarget(value: ISolicitudModalidadBackend): ISolicitudModalidad {
    if (!value) {
      return value as unknown as ISolicitudModalidad;
    }
    return {
      id: value.id,
      solicitudId: value.solicitudId,
      entidad: { id: value.entidadRef } as IEmpresa,
      programa: value.programa
    };
  }

  fromTarget(value: ISolicitudModalidad): ISolicitudModalidadBackend {
    if (!value) {
      return value as unknown as ISolicitudModalidadBackend;
    }
    return {
      id: value.id,
      solicitudId: value.solicitudId,
      entidadRef: value.entidad?.id,
      programa: value.programa
    };
  }
}

export const SOLICITUD_MODALIDAD_CONVERTER = new SolicitudModalidadConverter();
