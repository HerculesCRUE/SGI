import { ISolicitudRrhh } from '@core/models/csp/solicitud-rrhh';
import { IEmpresa } from '@core/models/sgemp/empresa';
import { IClasificacion } from '@core/models/sgo/clasificacion';
import { SgiBaseConverter } from '@sgi/framework/core';
import { ISolicitudRrhhResponse } from './solicitud-rrhh-response';

class SolicitudRrhhResponseConverter
  extends SgiBaseConverter<ISolicitudRrhhResponse, ISolicitudRrhh> {
  toTarget(value: ISolicitudRrhhResponse): ISolicitudRrhh {
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

  fromTarget(value: ISolicitudRrhh): ISolicitudRrhhResponse {
    if (!value) {
      return value as unknown as ISolicitudRrhhResponse;
    }
    return {
      id: value.id,
      universidadRef: value.universidad?.id,
      areaAnepRef: value.areaAnep?.id,
      universidad: value.universidadDatos
    };
  }
}

export const SOLICITUD_RRHH_RESPONSE_CONVERTER = new SolicitudRrhhResponseConverter();
