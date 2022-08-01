import { ISolicitudRrhh } from '@core/models/csp/solicitud-rrhh';
import { IEmpresa } from '@core/models/sgemp/empresa';
import { IClasificacion } from '@core/models/sgo/clasificacion';
import { SgiBaseConverter } from '@sgi/framework/core';
import { ISolicitudRrhhRequest } from './solicitud-rrhh-request';

class SolicitudRrhhRequestConverter
  extends SgiBaseConverter<ISolicitudRrhhRequest, ISolicitudRrhh> {
  toTarget(value: ISolicitudRrhhRequest): ISolicitudRrhh {
    if (!value) {
      return value as unknown as ISolicitudRrhh;
    }
    return {
      id: value.id,
      universidad: value.universidadRef ? { id: value.universidadRef } as IEmpresa : null,
      areaAnep: value.areaAnepRef ? { id: value.areaAnepRef } as IClasificacion : null,
      universidadDatos: value.universidad
    };
  }

  fromTarget(value: ISolicitudRrhh): ISolicitudRrhhRequest {
    if (!value) {
      return value as unknown as ISolicitudRrhhRequest;
    }
    return {
      id: value.id,
      universidadRef: value.universidad?.id,
      areaAnepRef: value.areaAnep?.id,
      universidad: value.universidadDatos
    };
  }
}

export const SOLICITUD_RRHH_REQUEST_CONVERTER = new SolicitudRrhhRequestConverter();
